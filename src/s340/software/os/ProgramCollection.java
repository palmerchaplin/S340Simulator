/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s340.software.os;

import java.util.LinkedList;
import java.util.List;
import s340.hardware.Machine;
import s340.software.os.Program;

/**
 *
 * @author palmerchaplin
 */
public class ProgramCollection
{
//PROJECT 1 PROGRAMS

    public static Program Program1()
    {
        ////////TEST1/////
        //Program starts at position 10 and ends at position 60
        ProgramBuilder b3 = new ProgramBuilder();
        int sum = 900;
        int max = 902;

        int i = 904;
        b3.start(10);
        b3.loadi(0);
        b3.store(sum); // sum = 0
        b3.loadi(101);
        b3.store(max); // max = 0

        b3.loadi(1); // acc = 1
        b3.tax(); // x = acc 
        b3.loadi(1); // acc = 1
        b3.sub(max);
        b3.jpos(54);
        b3.jzero(54);

        b3.txa();
        b3.store(i);
        b3.load(sum);
        b3.add(i);
        b3.store(sum);
        b3.load(i);
        b3.tax();
        b3.incx();

        b3.load(max);
        b3.subi(1);
        b3.store(max);

        b3.jmp(22);

        b3.load(sum);

        b3.output();
        b3.end();

        return b3.build();

    }

    public static Program Program2()
    {
        ////// TEST 2 ///////
        //This program starts at 100 and ends at 148 
        ProgramBuilder b2 = new ProgramBuilder();
        int j = 910;
        int k = 912;
        int max2 = 914;
        b2.start(100);
        b2.loadi(1);
        b2.store(j); //j = 1
        b2.loadi(5);
        b2.store(max2); // max = 5
        b2.load(j);
        b2.sub(max2);  // while(j <= max)

        b2.jpos(146); // jump to end of outer loop 
        b2.loadi(1);
        b2.store(k); // k = 1
        b2.load(k);
        b2.sub(max2); // while(k <= max)
        b2.jpos(138); //jump to end of inner loop

        b2.load(k);
        b2.mul(j); // acc = j * k
        b2.output(); // System.out.println(acc);
        b2.load(k);
        b2.inca(); // k+= 1
        b2.store(k);
        b2.jmp(118); /// jump to start of inner loop

        b2.load(j);
        b2.inca();
        b2.store(j); // j+= 1
        b2.jmp(108); // jump to start of outer loop

        b2.end();

        return b2.build();
    }

    public static Program Program3()
    {
        /////TEST 3/////
        //this program starts at 200 and ends at 256
        ProgramBuilder b3 = new ProgramBuilder();
        int sum = 920;
        int max = 922;
        int i = 924;
        int iSquared = 926;

        b3.start(200);
        b3.loadi(0);
        b3.store(sum);
        b3.loadi(101);
        b3.store(max);
        b3.loadi(1);
        b3.tax();
        b3.loadi(1);
        b3.sub(max);
        b3.jpos(250);
        b3.jzero(250);
        b3.txa();
        b3.store(i);
        b3.load(i);
        b3.mul(i); // i * i (i squared)
        b3.store(iSquared);
        b3.load(sum);
        b3.add(iSquared);
        b3.store(sum);
        b3.load(i);
        b3.tax();
        b3.incx();
        b3.load(max);
        b3.subi(1);
        b3.store(max);
        b3.jmp(212);
        b3.load(sum);
        b3.output();
        b3.end();

        return b3.build();
    }

    public static Program Program4()
    {
        //////TEST 4//////
        //Starts at 300 and ends at
        ProgramBuilder b4 = new ProgramBuilder();

        int j = 930;
        int k = 932;
        int max = 934;
        b4.start(300);
        b4.loadi(31);
        b4.store(j); //j = 31
        b4.loadi(35);
        b4.store(max); // max = 35
        b4.load(j);
        b4.sub(max);  // while(j <= max)

        b4.jpos(346); // jump to end of outer loop 
        b4.loadi(31);
        b4.store(k); // k = 31
        b4.load(k);
        b4.sub(max); // while(k <= max)
        b4.jpos(338); //jump to end of inner loop

        b4.load(k);
        b4.mul(j); // acc = j * k
        b4.output(); // System.out.println(acc);
        b4.load(k);
        b4.inca(); // k+= 1
        b4.store(k);
        b4.jmp(318); /// jump to start of inner loop

        b4.load(j);
        b4.inca();
        b4.store(j); // j+= 1
        b4.jmp(308); // jump to start of outer loop

        b4.end();

        return b4.build();
    }

//PROJECT 2 PROGRAMS
    //This program was used to test virtual and physical addresses in memory
    public static Program pSubs(int num)
    {
        ProgramBuilder b5 = new ProgramBuilder();

        b5.size(10);

        b5.loadi(num);
        for (int i = 48; i < 58; i++)
        {
            b5.store(i);
        }

        b5.loadi(0);

        for (int j = 48; j < 58; j++)
        {
            b5.add(j);
        }
        b5.output();
        b5.end();

        return b5.build();
    }

    //EXPAND/MERGE TEST 
    /*These mini programs are used to test our merge and expand program.
      we will create two of these mini programs 
      these programs will finish before our "moreMem" program that will eventually ask for more memory.
     */
    public static Program miniProg(int output)
    {
        ProgramBuilder b1 = new ProgramBuilder();
        b1.size(0);
        b1.loadi(output);
        b1.output();
        b1.end();

        return b1.build();
    }

//SHIFTING TEST  
    //this program is used to test our shiftProgram and should fail because we store more than the requested memory
    public static Program moreMem()
    {
        ProgramBuilder b1 = new ProgramBuilder();
        b1.size(0);
        b1.loadi(4);
        b1.syscall(SystemCall.SBRK); //ask for 4 more blocks of memory
        b1.loadi(20);

        //stores 20 into virtual addresses 22-26
        b1.store(22);
        b1.store(23);
        b1.store(24);
        b1.store(25);
        b1.store(26); //this will fail because our program only requested 4 more spaces of memory

        //outputs 20
        b1.output();
        b1.end();
        System.out.println(b1.build());
        return b1.build();
    }

    //this is a program to test that our shiftProgram works
    public static Program moreMem2()
    {
        ProgramBuilder b1 = new ProgramBuilder();
        b1.size(0); //initial size is 0 so our program can’t store any data
        b1.loadi(1);
        b1.loadi(4);
        b1.syscall(SystemCall.SBRK); //programmer ask for 4 more spaces of memory
        b1.loadi(20);

        //to test that our syscall worked we should now be able to store into these 4 memory locations.
        b1.store(20);
        b1.store(21);
        b1.store(22);
        b1.store(23);

        b1.output();
        b1.end();
        System.out.println(b1.build());
        return b1.build();
    }

//This program is designed to take a long time to terminate, as well as take a substantial amount of memory. This will force our moreMem programs to do a shift rather than expand.
    public static Program longProg()
    {
        ProgramBuilder b1 = new ProgramBuilder();
        b1.size(0);
        b1.loadi(0);
        b1.loadi(0);
        b1.loadi(0);
        b1.loadi(0);
        b1.loadi(0);
        b1.loadi(0);
        b1.loadi(0);
        b1.loadi(0);
        b1.loadi(0);
        b1.loadi(0);
        b1.loadi(0);
        b1.loadi(0);
        b1.loadi(1);
        b1.output();
        b1.end();

        return b1.build();
    }

    //COMPACTION TESTING 
    //p3 = 6, p1 = 6
    public static Program waitProcess(int memoryStorage)
    {
        ProgramBuilder b1 = new ProgramBuilder();
        b1.size(memoryStorage);
//      b1.loadi(0);
        b1.jmp(0);
        b1.end();
        return b1.build();
    }

    public static Program moreMem3(int memNeeded)
    {
        ProgramBuilder b1 = new ProgramBuilder();
        b1.size(0); //initial size is 0 so our program can’t store any data
//        b1.loadi(1);
//        b1.loadi(1);
//        b1.loadi(1);
//        b1.loadi(1);
//        b1.loadi(1);
        b1.loadi(1);
        b1.loadi(memNeeded);

        b1.syscall(SystemCall.SBRK); //programmer ask for 4 more spaces of memory

        b1.loadi(40);

        b1.store(50);
        b1.store(51);
        b1.store(52);
        b1.store(53);

        b1.output();
        b1.end();
        System.out.println(b1.build());
        return b1.build();
    }

    //p1 = 13, p2 = 16
    public static Program mini(int size)
    {
        ProgramBuilder b1 = new ProgramBuilder();
        b1.size(size);
        b1.output();
        b1.end();

        return b1.build();
    }

    //PROGRAMS FOR ASSIGNMENT 3
    public static Program writeToScreen()
    {
        ProgramBuilder b1 = new ProgramBuilder();
        b1.size(0);
        b1.loadi(50);
        b1.syscall(SystemCall.WRITE_CONSOLE);
        b1.syscall(SystemCall.WRITE_CONSOLE);
        b1.end();

        return b1.build();
    }

    public static Program writeConsole(int numStored)
    {
        ProgramBuilder b1 = new ProgramBuilder();
        b1.size(20);
        b1.loadi(numStored);
        b1.store(40);
        b1.store(41);
        b1.store(42);
        b1.store(43);
        b1.store(44);

        b1.load(40);
        b1.syscall(SystemCall.WRITE_CONSOLE);
        b1.load(41);
        b1.syscall(SystemCall.WRITE_CONSOLE);
        b1.load(42);
        b1.syscall(SystemCall.WRITE_CONSOLE);
        b1.load(43);
        b1.syscall(SystemCall.WRITE_CONSOLE);
        b1.load(44);
        b1.syscall(SystemCall.WRITE_CONSOLE);

        b1.end();
        System.out.println(b1.build());
        return b1.build();
    }

    public static Program pReadDisk()
    {
        int deviceNumber = 2;
        int platterNumber = 3;
        int start = 0;
        int length = 3;
        int memoryLocation = 150;

        int startMemory = 100;

        ProgramBuilder b1 = new ProgramBuilder();
        b1.size(20);
        b1.loadi(deviceNumber);
        b1.store(startMemory);
        b1.loadi(platterNumber);
        b1.store(startMemory + 1);
        b1.loadi(start);
        b1.store(startMemory + 2);
        b1.loadi(length);
        b1.store(startMemory + 3);
        b1.loadi(memoryLocation);
        b1.store(startMemory + 4);

        b1.loadi(startMemory);

        b1.syscall(SystemCall.READ_DISK);

        b1.end();
        return b1.build();
    }

    public static Program pWriteDisk(int valueStored)
    {
        int deviceNumber = 2;
        int platterNumber = 3;
        int start = 0;
        int length = 3;
        int memoryLocation = 60;

        int startMemory = 50;

        ProgramBuilder b1 = new ProgramBuilder();
        b1.size(30);

        //these are the values we want to put in the disk
        b1.loadi(valueStored);
        b1.store(memoryLocation);
        b1.loadi(valueStored + 1);
        b1.store(memoryLocation + 1);
        b1.loadi(valueStored + 2);
        b1.store(memoryLocation + 2);

        //pass our parameters in.
        b1.loadi(deviceNumber);
        b1.store(startMemory);
        b1.loadi(platterNumber);
        b1.store(startMemory + 1);
        b1.loadi(start);
        b1.store(startMemory + 2);
        b1.loadi(length);
        b1.store(startMemory + 3);
        b1.loadi(memoryLocation);
        b1.store(startMemory + 4);

        b1.loadi(startMemory);
        //b1.syscall(SystemCall.WRITE_CONSOLE);
        b1.syscall(SystemCall.WRITE_DISK);

        b1.loadi(1);

        b1.end();

        return b1.build();

    }

    public static Program pWriteDisk2(int valueStored)
    {
        int deviceNumber = 2;
        int platterNumber = 3;
        int start = 0;
        int length = 4;
        int memoryLocation = 60;

        int startMemory = 50;

        ProgramBuilder b1 = new ProgramBuilder();
        b1.size(30);

        //these are the values we want to put in the disk
        b1.loadi(valueStored);
        b1.store(memoryLocation);
        b1.loadi(valueStored + 1);
        b1.store(memoryLocation + 1);
        b1.loadi(valueStored + 2);
        b1.store(memoryLocation + 2);
        b1.loadi(valueStored + 3);
        b1.store(memoryLocation + 3);

        //pass our parameters in.
        b1.loadi(deviceNumber);
        b1.store(startMemory);
        b1.loadi(platterNumber);
        b1.store(startMemory + 1);
        b1.loadi(start);
        b1.store(startMemory + 2);
        b1.loadi(length);
        b1.store(startMemory + 3);
        b1.loadi(memoryLocation);
        b1.store(startMemory + 4);

        b1.loadi(startMemory);
        //b1.syscall(SystemCall.WRITE_CONSOLE);
        b1.syscall(SystemCall.WRITE_DISK);

        b1.loadi(1);

        b1.end();

        return b1.build();

    }

    public static Program WriteReadDisk()
    {
        int value = 1;
        int deviceNumber = 2;
        int platterNumber = 3;
        int start = 31; //where on disk to write to
        int length = 20;
        int memoryLocation = 201; //the start location of memory that buffer reads

        //maybe change this line of code
        int startRegisters = 250; //can this be 0 since other instructions have already been ran by the time we do this? or cshould we be concerned with overwriting instructions

        ProgramBuilder b1 = new ProgramBuilder();
        b1.size(300);

//        for(int i = 81;i<101;i++){
//            b1.loadi(value);
//            b1.store(i);
//            value++;
//        }
        for (int i = 201; i < 221; i++)
        {
            b1.loadi(value);
            b1.store(i);
            value++;
        }

        b1.loadi(deviceNumber);
        b1.store(startRegisters);
        b1.loadi(platterNumber);
        b1.store(startRegisters + 1);
        b1.loadi(start);
        b1.store(startRegisters + 2);
        b1.loadi(length);
        b1.store(startRegisters + 3);
        b1.loadi(memoryLocation);
        b1.store(startRegisters + 4);

        b1.loadi(startRegisters);

        b1.syscall(SystemCall.WRITE_DISK);

        memoryLocation = 231;
        b1.loadi(memoryLocation);
        b1.store(startRegisters + 4);

        b1.loadi(startRegisters);
        b1.syscall(SystemCall.READ_DISK);
        for (int i = 231; i < 251; i++)
        {
            b1.load(i);
            b1.syscall(SystemCall.WRITE_CONSOLE);
        }

        b1.loadi(100);
        b1.output();
        b1.end();
        System.out.println(b1.build());
        return b1.build();
    }

    public static Program testSSTF(int value, int _s, int _l)
    {
        int deviceNumber = Machine.DISK1;
        int platterNumber = 3;
        int start = _s;
        int length = _l;
        int memoryLocation = 60;

        int startMemory = 50;

        ProgramBuilder b1 = new ProgramBuilder();

        b1.size(50);
        b1.loadi(value);
        for (int i = memoryLocation; i < memoryLocation + length; i++)
        {
            b1.store(i);

        }
        //pass our parameters in.
        b1.loadi(deviceNumber);
        b1.store(startMemory);
        b1.loadi(platterNumber);
        b1.store(startMemory + 1);
        b1.loadi(start);
        b1.store(startMemory + 2);
        b1.loadi(length);
        b1.store(startMemory + 3);
        b1.loadi(memoryLocation);
        b1.store(startMemory + 4);

        b1.loadi(startMemory);

        b1.syscall(SystemCall.WRITE_DISK);

        b1.end();

        return b1.build();
    }
}
