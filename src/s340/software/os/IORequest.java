/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s340.software.os;

/**
 *
 * @author palmerchaplin
 */
public class IORequest {
    private int programIndex;
    private int operation;
    
    private int start; 
    private int length;

    public IORequest(int programIndex,int operation) {
        this.programIndex = programIndex;
        this.operation = operation;
        start = 0;
        length= 0;
    }
    
    public IORequest(int programIndex,int operation,int start,int length) {
        this(programIndex,operation);
        this.start = start;
        this.length = length;
    }

    public int getStart() {
        return start;
    }

    public int getLength() {
        return length;
    }
    
    

    public int getProgramIndex() {
        return programIndex;
    }

    public int getOperation() {
        return operation;
    }

    @Override
    public String toString()
    {
        return "IORequest{" + "programIndex=" + programIndex + ", operation=" + operation + ", start=" + start + ", length=" + length + '}';
    }

 
    
    
    
    

    
}