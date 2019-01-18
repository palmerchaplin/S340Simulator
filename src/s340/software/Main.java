package s340.software;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import s340.hardware.Machine;
import s340.software.os.OperatingSystem;
import s340.software.os.Program;
import s340.software.os.ProgramCollection;

/**
 *
 */
public class Main
{

    public static void main(String[] args) throws Exception
    {
        //  setup the hardware, the operating system, and power up
        //	do not remove this
        Machine machine = new Machine();
        OperatingSystem os = new OperatingSystem(machine);
        machine.powerUp(os);
        // create a program

        //grab a program from collection, add to list, schedule list of programs
        List<Program> programs = new LinkedList<>();

////////Testing virtua/physical
//          programs.add(ProgramCollection.A2P1(0));
//          programs.add(ProgramCollection.A2P1(1));
//          programs.add(ProgramCollection.A2P1(2));
//          programs.add(ProgramCollection.A2P1(3));
//          programs.add(ProgramCollection.A2P1(4));
///////////Testing expand
//          programs.add(ProgramCollection.miniProg(2));
//          programs.add(ProgramCollection.miniProg(3));
//          programs.add(ProgramCollection.moreMem2());
/////////Testing Shift
//          programs.add(ProgramCollection.moreMem2());
//          programs.add(ProgramCollection.longProg());
        /*	Testing Compaction
            this test will have two mini programs that finish before our more memory program
            asks for more memory. It will also have two additional wait processes that never finish
            (one that needs to shift left and one too shift right). Our moreMem3 prog will ask for 
            more memory and then try storing into these memory locations.
         */
//            programs.add(ProgramCollection.mini(6));
//            programs.add(ProgramCollection.mini(6));
//            programs.add(ProgramCollection.waitProcess(6));
//            programs.add(ProgramCollection.moreMem3(240));
//            programs.add(ProgramCollection.waitProcess(6));
//        programs.add(ProgramCollection.testIt(1));
//        programs.add(ProgramCollection.testIt(2));
//        programs.add(ProgramCollection.testIt(3));
//        programs.add(ProgramCollection.writeConsole(1));
//        programs.add(ProgramCollection.writeConsole(2));
//        programs.add(ProgramCollection.writeConsole(3));
//          programs.add(ProgramCollection.pWriteDisk2(340));
//TEST READ AND WRITE DISK
        //programs.add(ProgramCollection.WriteReadDisk());
//TEST SSTF
//        programs.add(ProgramCollection.testSSTF(10, 46, 5));
//        programs.add(ProgramCollection.testSSTF(20, 71, 5));
//        programs.add(ProgramCollection.testSSTF(30, 51, 10));
//        programs.add(ProgramCollection.testSSTF(40, 26, 5));

//TEST STARVATION
        programs.add(ProgramCollection.testSSTF(10, 31, 3));
        programs.add(ProgramCollection.testSSTF(340, 90, 3));
        programs.add(ProgramCollection.testSSTF(20, 51, 3));
        programs.add(ProgramCollection.testSSTF(30, 41, 3));
        programs.add(ProgramCollection.testSSTF(40, 51, 3));
        programs.add(ProgramCollection.testSSTF(50, 41, 3));
        programs.add(ProgramCollection.testSSTF(60, 51, 3));
        programs.add(ProgramCollection.testSSTF(70, 41, 3));
        programs.add(ProgramCollection.testSSTF(80, 51, 3));
        programs.add(ProgramCollection.testSSTF(90, 41, 3));

        os.schedule(programs);

    }

}
