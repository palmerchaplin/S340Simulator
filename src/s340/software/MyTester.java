/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s340.software;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;


/**
 *
 * @author PalmerChaplin
 */
public class MyTester {
    private static List<FreeSpace> freeSpaces;
    
    
    public static void main(String[] args) throws Exception{
        
    FreeSpace f1 = new FreeSpace(0,11);
    FreeSpace f2 = new FreeSpace(11,6);
    FreeSpace f3 = new FreeSpace(20,6);
    FreeSpace f4 = new FreeSpace(26,11);
    FreeSpace f5 = new FreeSpace(37,4);
    FreeSpace f6 = new FreeSpace(45,5);
    FreeSpace f7 = new FreeSpace(50,6);
    FreeSpace f8 = new FreeSpace(60,6);
    
    freeSpaces = new ArrayList<FreeSpace>();
    freeSpaces.add(f1);
    freeSpaces.add(f2);
    freeSpaces.add(f3);
    freeSpaces.add(f4);
    freeSpaces.add(f5);
    freeSpaces.add(f6);
    freeSpaces.add(f7);
    freeSpaces.add(f8);
    
    merge();
        System.out.println(freeSpaces);
    }
    
    public static void merge()
    {
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
    
    
    
}