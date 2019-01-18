package s340.software.os;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.logging.Level;
import java.util.logging.Logger;
import s340.hardware.DeviceControllerOperations;
import s340.hardware.IInterruptHandler;
import s340.hardware.ISystemCallHandler;
import s340.hardware.ITrapHandler;
import s340.hardware.Machine;
import s340.hardware.Trap;
import s340.hardware.exception.MemoryFault;
import s340.software.FreeSpace;
import s340.software.os.IORequest;
import s340.software.ProcessControlBlock;
import s340.software.ProcessState;
import s340.software.SSTF;

/*
 * The operating system that controls the software running on the S340 CPU.
 *
 * The operating system acts as an interrupt handler, a system call handler, and
 * a trap handler.
*
* @author Daniel Suarez, Palmer Chaplin and Giovanni Flores
 */
public class OperatingSystem implements IInterruptHandler, ISystemCallHandler, ITrapHandler
{

    // the machine on which we are running.
    private final Machine machine;

    private static final int MAX_TABLE_SIZE = 10; // maximum of 10 processes

    private ProcessControlBlock[] processTable;
    private static final int MAX_MEMORY = Machine.MEMORY_SIZE;

    private List<FreeSpace> freeSpaces;

    private LinkedList<IORequest>[] deviceQueues;

    private int currentProcess;

    /*
	 * Create an operating system on the given machine.
     */
    public OperatingSystem(Machine machine) throws MemoryFault
    {
        this.machine = machine;
        this.currentProcess = 0;
        this.processTable = new ProcessControlBlock[MAX_TABLE_SIZE]; // creates new process table, which is full of nulls at this point
        this.freeSpaces = new ArrayList<>();
        this.freeSpaces.add(new FreeSpace(0, MAX_MEMORY));
        this.deviceQueues = new LinkedList[Machine.NUM_DEVICES];
        for (int i = 0; i < this.deviceQueues.length; i++)
        {
            deviceQueues[i] = new LinkedList();
        }

        ///initialize the wait queues correctly
        List<Program> programs = new LinkedList<>();
        programs.add(waitProcess());
        schedule(programs); //make sure the wait program is always scheduled
    }

    private boolean expandProgram(int memoryNeeded)
    {
        int currentBase = this.processTable[currentProcess].getBase();
        int currentLimit = this.processTable[currentProcess].getLimit();

        for (FreeSpace f : freeSpaces)
        {
            /*
            checks to see if the free space is big enough and is directly after the given program
             */
            if (f.getLimit() >= memoryNeeded && f.getBase() == (currentBase + currentLimit))
            {

                // -- grow limit of the process
                this.processTable[currentProcess].setLimit(currentLimit + memoryNeeded);

                // -- we need to reduce the free space after we've expanded our process
                f.setBase(f.getBase() + memoryNeeded);
                f.setLimit(f.getLimit() - memoryNeeded);

                return true;
            }

        }
        return false;
    }

    private boolean shiftProgram(int memoryNeeded)
    {
        int currentBase = this.processTable[currentProcess].getBase();
        int currentLimit = this.processTable[currentProcess].getLimit();

        // -- finds a free space 
        FreeSpace f1 = this.findFreeSpace(currentLimit + memoryNeeded);
        if (f1 != null)
        /* -- we don't want to do the below if there isn't an available free space big enough. 
                            We want to compact instead*/ {

            int freeSpaceBase = f1.getBase();
            /*
                we need to get the instructions of the program from the base to the end (end = limit + base)
             */
            shiftLeft(currentBase, currentLimit, freeSpaceBase);
            /*
               change the old values to the new ones
             */
            this.processTable[currentProcess].setBase(currentBase);
            this.processTable[currentProcess].setLimit(currentLimit + memoryNeeded);

            /*
                once the program is in the free space, we have to redefine limits. 
             */
            f1.setBase(f1.getBase() + memoryNeeded + currentLimit);
            f1.setLimit(f1.getLimit() - (memoryNeeded + currentLimit));
            // -- add a new free space where the program was
            freeSpaces.add(new FreeSpace(currentBase, currentLimit));

            return true;
        }
        return false;
    }

    private int sbrk(int memoryNeeded)
    {

        if (expandProgram(memoryNeeded))
        {

            return 0;
        }
        merge();

        if (expandProgram(memoryNeeded))
        {

            return 0;

        }
        this.machine.memory.setBase(0);
        this.machine.memory.setLimit(MAX_MEMORY);
        if (shiftProgram(memoryNeeded))
        {

            return 0;
        }

        if (compact(memoryNeeded))
        {

            return 0;

        }

        //if compaction fails (meaning there are no available free spaces no matter what we do)
        return 1;
    }

    private int shiftLeft(int currentBase, int currentLimit, int a)
    {
        for (int k = currentBase; k < (currentLimit + currentBase); k++)
        {
            try
            {
                this.machine.memory.store(a++, this.machine.memory.load(k));
            } catch (MemoryFault ex)
            {
                Logger.getLogger(OperatingSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return a;
    }

    private int shiftRight(int currentBase, int currentLimit, int b)
    {
        for (int k = currentBase + currentLimit - 1; k >= currentBase; k--)
        {
            try
            {

                this.machine.memory.store(b, this.machine.memory.load(k));
                b--;
            } catch (MemoryFault ex)
            {
                Logger.getLogger(OperatingSystem.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        return b;
    }

    private boolean compact(int memoryNeeded)
    {

        int A = 4;
        int B = MAX_MEMORY - 1;

        /* 
            create a copy of our process table, only that we only add the processes 
           that haven't ended and we ignore the nulls
         */
        List<ProcessControlBlock> processTableCopy = new ArrayList<>();
        for (int i = 1; i < processTable.length; i++)
        {
            if (processTable[i] != null && processTable[i].getStatus() != ProcessState.END)
            {
                processTableCopy.add(processTable[i]);
            }
        }

        Collections.sort(processTableCopy);
        int savedBase = -1;
        for (int i = 0; i < processTableCopy.size(); i++)
        {

            int currentLimit = processTableCopy.get(i).getLimit();

            // shift left all of the programs whose base is less than that of the more memory program
            if (processTableCopy.get(i).getBase() <= processTable[currentProcess].getBase())
            {

                A = shiftLeft(processTableCopy.get(i).getBase(), currentLimit, A); // changed from current base
                savedBase = processTableCopy.get(i).getBase();
                processTableCopy.get(i).setBase(A - currentLimit);

                //lastProcessMovedLeft = i;
            }
            if (savedBase == processTable[currentProcess].getBase())
            {
                break;
            }
        }

        for (int i = processTableCopy.size() - 1; i > 0; i--)
        {

            int currentBase = processTableCopy.get(i).getBase();

            int currentLimit = processTableCopy.get(i).getLimit();
            // shift right all of the programs whose base is greater than that of the more memory program
            if (processTableCopy.get(i).getBase() > processTable[currentProcess].getBase())
            {
                B = shiftRight(currentBase, currentLimit, B);
                B++;
                processTableCopy.get(i).setBase(B);
            }
            if (savedBase == processTable[currentProcess].getBase())
            {
                break;
            }
        }
        freeSpaces.removeAll(freeSpaces);

        FreeSpace f = new FreeSpace(A, B - A);

        freeSpaces.add(f);

        // -- we want to make all the free spaces available
        return this.expandProgram(memoryNeeded);

    }

    private void merge()
    {
        // -- sorted by base
        Collections.sort(freeSpaces);

        Iterator it = freeSpaces.iterator();
        FreeSpace previous = null;
        if (it.hasNext())
        {
            previous = (FreeSpace) it.next();
        }
        while (it.hasNext())
        {
            FreeSpace current = (FreeSpace) it.next();
            if (previous.getBase() + previous.getLimit() == current.getBase())
            {
                int limit = previous.getLimit() + current.getLimit();
                it.remove();

                previous.setLimit(limit);

            } else
            {
                previous = current;
            }

        }
    }

    private void printProcessAndSpaces()
    {
        System.out.println();
        System.out.println("Process Table");
        System.out.println("---------------");
        for (int i = 0; i < this.processTable.length; i++)
        {
            if (this.processTable[i] != null && this.processTable[i].getStatus() != ProcessState.END)
            { //ended????
                System.out.println("Process Number : " + i + "\tBase: " + this.processTable[i].getBase() + "   Limit: " + this.processTable[i].getLimit());

            }

        }
        System.out.println();
        System.out.println();
        System.out.println("Free Space List");
        System.out.println("---------------");
        for (int i = 0; i < freeSpaces.size(); i++)
        {
            System.out.println("Free Space Number: " + i + "\tBase: " + freeSpaces.get(i).getBase() + "\tLimit: " + freeSpaces.get(i).getLimit());
        }
    }

    /**
     * For diagnostic purposes
     */
    private void printDeviceQueues()
    {

        System.out.println();
        System.out.println("Device Queues");
        System.out.println("-----------------------------------");

        for (List<IORequest> queue : deviceQueues)
        {
            queue.forEach((ioRequest) ->
            {
                System.out.println(ioRequest);
            });

        }

    }

    public FreeSpace findFreeSpace(int size)
    {
        for (FreeSpace freeSpace : freeSpaces)
        {
            if (freeSpace.getLimit() >= size)
            {
                return freeSpace;
            }
        }
        return null;
    }

    // -- wait program that loops infinitely when no other processes are ready to run
    private static Program waitProcess()
    {
        ProgramBuilder b1 = new ProgramBuilder();
        b1.jmp(0);
        b1.end();
        return b1.build();
    }

    // scans the process table and chooses the next process to run. 
    public int chooseNextProcess()
    {

        // start at current process + 1 so we don't choose the same process
        for (int i = currentProcess + 1; i < processTable.length; i++)
        {
            if (this.processTable[i] != null && this.processTable[i].getStatus() == ProcessState.READY)
            {
                this.processTable[i].setStatus(ProcessState.RUNNING);
                return i;
            }

        }
        //check the programs that may come before the current processes, and check if they're ready 
        for (int j = 1; j <= currentProcess; j++)
        {

            if (this.processTable[j] != null && this.processTable[j].getStatus() == ProcessState.READY)
            {
                this.processTable[j].setStatus(ProcessState.RUNNING);
                return j;

            }

        }
        // if all process are finished, go to the wait program
        this.processTable[0].setStatus(ProcessState.RUNNING);
        return 0;

    }

    /*
	 * Load a program into a given memory address
     */
    private int loadProgram(Program program, int programIndex) throws MemoryFault
    {

        int address = processTable[programIndex].getBase();

        for (int i : program.getCode())
        {
            machine.memory.store(address++, i);
        }

        return address;
    }

    /*
	 * Scheduled a list of programs to be run.
	 * 
	 * 
	 * @param programs the programs to schedule
     */
    public synchronized void schedule(List<Program> programs) throws MemoryFault
    {

        this.machine.memory.setBase(0);
        this.machine.memory.setLimit(MAX_MEMORY);
        for (Program program : programs)
        {

            //find an open process(null or END)
            for (int i = 0; i < this.processTable.length; i++)
            {

                if (this.processTable[i] == null || this.processTable[i].getStatus() == ProcessState.END)
                {
                    int programSize = program.getDataSize() + program.getCode().length;

                    FreeSpace f = this.findFreeSpace(programSize);

                    this.processTable[i] = new ProcessControlBlock(f.getBase(), programSize);

                    f.setBase(programSize + f.getBase());

                    f.setLimit(f.getLimit() - programSize);

                    loadProgram(program, i);

                    break;
                }

            }

        }
        //this.printProcessAndSpaces();
        // leave this as the last line
        machine.cpu.runProg = true;
    }

    //save the current processes' registers in the process table.
    private void saveRegisters(int pc)
    {

        this.processTable[currentProcess].setPc(pc);
        this.processTable[currentProcess].setAcc(this.machine.cpu.acc);
        this.processTable[currentProcess].setX(this.machine.cpu.x);

    }

    //puts back current process registers in machine cpu
    private void restoreRegisters()
    {

        this.machine.cpu.acc = this.processTable[currentProcess].getAcc();
        this.machine.cpu.x = this.processTable[currentProcess].getX();
        this.machine.memory.setBase(this.processTable[currentProcess].getBase());
        this.machine.memory.setLimit(this.processTable[currentProcess].getLimit());
        this.machine.cpu.setPc(this.processTable[currentProcess].getPc());
    }

    /*
    * Handle a trap from the hardware.
    * 
    * @param programCounter -- the program counter of the instruction after the
    * one that caused the trap.
    * 
    * @param trapNumber -- the trap number for this trap.
     */
    @Override
    public synchronized void trap(int savedProgramCounter, int trapNumber)
    {
        //  leave this code here
        CheckValid.trapNumber(trapNumber);
        if (!machine.cpu.runProg)
        {
            return;
        }
        //  end of code to leave

        saveRegisters(savedProgramCounter);
        switch (trapNumber)
        {
            case Trap.TIMER:

                this.processTable[currentProcess].setStatus(ProcessState.READY); //make sure current process is set to Ready before choosing a new process

                break;
            case Trap.END:

                this.processTable[currentProcess].setStatus(ProcessState.END); //make sure current process is no longer chosen.
                FreeSpace freeSpace = new FreeSpace(this.processTable[currentProcess].getBase(),
                        this.processTable[currentProcess].getLimit());
                this.freeSpaces.add(freeSpace);

                break;
            case Trap.DIV_ZERO:
                this.processTable[currentProcess].setStatus(ProcessState.END); //make sure current process is no longer chosen.
                FreeSpace fs = new FreeSpace(this.processTable[currentProcess].getBase(),
                        this.processTable[currentProcess].getLimit());
                this.freeSpaces.add(fs);

                break;
            default:
                System.err.println("UNHANDLED TRAP " + trapNumber);
                System.exit(1);
        }
        currentProcess = this.chooseNextProcess();
        this.restoreRegisters();

    }

    /*
	 * Handle a system call from the software.
	 * 
	 * @param programCounter -- the program counter of the instruction after the
	 * one that caused the trap.
	 * 
	 * @param callNumber -- the callNumber of the system call.
	 * 
	 * @param address -- the memory address of any parameters for the system
	 * call.
     */
    @Override
    public synchronized void syscall(int savedProgramCounter, int callNumber)
    {
        try
        {
            //  leave this code here

            CheckValid.syscallNumber(callNumber);
            if (!machine.cpu.runProg)
            {
                return;
            }
            //  end of code to leave

            this.saveRegisters(savedProgramCounter);

            // -- needed to know what queue/list to add requests to
            int deviceNumber = this.machine.memory.load(this.processTable[currentProcess].getAcc());

            // -- SBRK's needs are different from that of the devices, so we took it out of the switch statement
            if (callNumber == SystemCall.SBRK)
            {
                this.processTable[currentProcess].setAcc(this.sbrk(this.machine.cpu.acc));
                this.printProcessAndSpaces();
            } else
            {

                this.processTable[currentProcess].setStatus(ProcessState.WAITING); // set to waiting until it finishes its operation
                switch (callNumber)
                {
                    // -- only start IO if there's nothing on the queue. Once started, always instantiate and add a request
                    case SystemCall.WRITE_CONSOLE:

                        if (this.deviceQueues[Machine.CONSOLE].isEmpty())
                        {
                            write_console(this.processTable[currentProcess].getAcc()); // -- acc is what we want to print
                        }
                        this.deviceQueues[Machine.CONSOLE].add(new IORequest(currentProcess, DeviceControllerOperations.WRITE));

                        break;
                    // -- read/write requests need the start and length
                    case SystemCall.READ_DISK:

                        IORequest reqRead = new IORequest(currentProcess, DeviceControllerOperations.READ,
                                this.machine.memory.load(this.processTable[currentProcess].getAcc() + 2),
                                this.machine.memory.load(this.processTable[currentProcess].getAcc() + 3));

                        if (this.deviceQueues[deviceNumber].isEmpty())
                        {
                            read_disk(reqRead);
                        }
                        this.deviceQueues[deviceNumber].add(reqRead);

                        break;
                    case SystemCall.WRITE_DISK:
                        IORequest reqWrite = new IORequest(currentProcess, DeviceControllerOperations.WRITE,
                                this.machine.memory.load(this.processTable[currentProcess].getAcc() + 2), // -- start
                                this.machine.memory.load(this.processTable[currentProcess].getAcc() + 3)); // -- length

                        if (this.deviceQueues[deviceNumber].isEmpty())
                        {
                            write_disk(reqWrite);
                        }
                        this.deviceQueues[deviceNumber].add(reqWrite);

                        break;

                }

                this.currentProcess = this.chooseNextProcess();

            }
            this.restoreRegisters();
        } catch (MemoryFault ex)
        {
            Logger.getLogger(OperatingSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
        System.out.println("\n\nEnd of syscall:\n\n");
        this.printDeviceQueues();
    }

    private void read_disk(IORequest request)
    {

        this.machine.memory.setBase(this.processTable[request.getProgramIndex()].getBase());
        this.machine.memory.setLimit(this.processTable[request.getProgramIndex()].getLimit());

        int[] parameters = this.extractParameters(request);
        this.machine.devices[parameters[1]].controlRegister.latch();

    }

    private void write_disk(IORequest request)
    {
        this.machine.memory.setBase(this.processTable[request.getProgramIndex()].getBase());
        this.machine.memory.setLimit(this.processTable[request.getProgramIndex()].getLimit());
        int[] parameters = this.extractParameters(request);

        this.memoryToBuffer(parameters[0], parameters[1], parameters[2]); //  acc, device number, and length

        this.machine.devices[parameters[1]].controlRegister.latch();

    }

    /**
     * This method gets the device number, platter, start and length, and puts
     * them into the control registers
     *
     * @param request
     * @return accumulator, device number and length, which we may need outside
     */
    private int[] extractParameters(IORequest request)
    {
        int[] needed = new int[3];
        try
        {
            int programAcc = this.processTable[request.getProgramIndex()].getAcc();

            int deviceNumber = this.machine.memory.load(programAcc);
            int platterNumber = this.machine.memory.load(programAcc + 1);
            int start = this.machine.memory.load(programAcc + 2);
            int length = this.machine.memory.load(programAcc + 3);

            this.machine.devices[deviceNumber].controlRegister.register[0] = request.getOperation();
            this.machine.devices[deviceNumber].controlRegister.register[1] = platterNumber;
            this.machine.devices[deviceNumber].controlRegister.register[2] = start;
            this.machine.devices[deviceNumber].controlRegister.register[3] = length;

            needed[0] = programAcc;
            needed[1] = deviceNumber;
            needed[2] = length;

        } catch (MemoryFault ex)
        {
            Logger.getLogger(OperatingSystem.class.getName()).log(Level.SEVERE, null, ex);

        }

        return needed;
    }

    private void write_console(int savedAcc)
    {
        this.machine.devices[Machine.CONSOLE].controlRegister.register[0] = DeviceControllerOperations.WRITE;
        this.machine.devices[Machine.CONSOLE].controlRegister.register[1] = savedAcc;
        this.machine.devices[Machine.CONSOLE].controlRegister.latch();
    }

    /*
	 * Handle an interrupt from the hardware.
	 * 
	 * @param programCounter -- the program counter of the instruction after the
	 * one that caused the trap.
	 * 
	 * @param deviceNumber -- the device number that is interrupting.
     */
    @Override
    public synchronized void interrupt(int savedProgramCounter, int deviceNumber)
    {
        //  leave this code here
        CheckValid.deviceNumber(deviceNumber);
        if (!machine.cpu.runProg)
        {
            return;
        }
        //  end of code to leave

        //clear int control register of device
        this.machine.interruptRegisters.register[deviceNumber] = false;

        //save registers for p1
        this.saveRegisters(savedProgramCounter);

        //take p1 off Q 
        IORequest finished = this.deviceQueues[deviceNumber].remove();

        //set p1 to ready
        this.processTable[finished.getProgramIndex()].setStatus(ProcessState.READY);

        switch (deviceNumber)
        {
            case Machine.CONSOLE:

                //check Q for a new prog to start writing/reading
                if (!this.deviceQueues[deviceNumber].isEmpty())
                {
                    int next = this.deviceQueues[deviceNumber].peek().getProgramIndex();
                    write_console(this.processTable[next].getAcc());
                }
                break;
            case Machine.DISK1:

                this.machine.memory.setBase(this.processTable[finished.getProgramIndex()].getBase());
                this.machine.memory.setLimit(this.processTable[finished.getProgramIndex()].getLimit());

                int operation = finished.getOperation();
                if (operation == DeviceControllerOperations.READ)
                {

                    bufferToMemory(deviceNumber, finished);

                }

                if (!this.deviceQueues[deviceNumber].isEmpty())
                {
                    try
                    {

                        int start = this.machine.memory.load(this.processTable[finished.getProgramIndex()].getAcc() + 2);
                        int length = this.machine.memory.load(this.processTable[finished.getProgramIndex()].getAcc() + 3);

                        this.deviceQueues[deviceNumber] = SSTF.execute((start + length) - 1, this.deviceQueues[deviceNumber]);
                        IORequest next = this.deviceQueues[deviceNumber].peek();
                        System.out.println("Next io to run: " + next);
                        switch (next.getOperation())
                        {
                            case DeviceControllerOperations.READ:
                                read_disk(next);
                            case DeviceControllerOperations.WRITE:
                                write_disk(next);
                        }
                    } catch (MemoryFault ex)
                    {
                        ex.printStackTrace();
                    }
                }

                break;

        }
        System.out.println("\n\nEnd of interrupt: \n\n");

        this.printDeviceQueues();
        this.restoreRegisters();

    }

    /* 
    for writing to the buffer from memory (write disk helper method)
    
     */
    private void memoryToBuffer(int programAcc, int deviceNumber, int length)
    {
        try
        {

            int memoryLocation = this.machine.memory.load(programAcc + 4);

            for (int i = 0; i < length; i++)
            {
                this.machine.devices[deviceNumber].buffer[i] = this.machine.memory.load(memoryLocation++);
            }

        } catch (MemoryFault ex)
        {
            Logger.getLogger(OperatingSystem.class.getName()).log(Level.SEVERE, null, ex);
        }

    }

    /**
     * for reading from the buffer to memory (read disk helper method)
     */
    private void bufferToMemory(int deviceNumber, IORequest req)
    {
        try
        {
            int programAcc = this.processTable[req.getProgramIndex()].getAcc();

            int length = this.machine.memory.load(this.processTable[req.getProgramIndex()].getAcc() + 3);
            int memoryLocation = this.machine.memory.load(programAcc + 4);

            for (int i = 0; i < length; i++)
            {

                this.machine.memory.store(memoryLocation++, this.machine.devices[deviceNumber].buffer[i]);
            }

        } catch (MemoryFault ex)
        {
            Logger.getLogger(OperatingSystem.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
