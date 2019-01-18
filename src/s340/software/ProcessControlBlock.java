/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package s340.software;

/**
 *
 * @author palmerchaplin
 */
public class ProcessControlBlock implements Comparable
{

    private int acc;
    private int x;
    private int pc;
    private int base;
    private int limit;

    private ProcessState status;

    public ProcessControlBlock(int base, int limit)
    {
        this.acc = 0;
        this.x = 0;
        this.pc = 0;
        this.status = ProcessState.READY;
        this.base = base;
        this.limit = limit;
    }

    public int getAcc()
    {
        return acc;
    }

    public int getX()
    {
        return x;
    }

    public int getPc()
    {
        return pc;
    }

    public ProcessState getStatus()
    {
        return this.status;
    }

    public void setAcc(int acc)
    {
        this.acc = acc;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public void setPc(int pc)
    {
        this.pc = pc;
    }

    public void setStatus(ProcessState status)
    {
        this.status = status;
    }

    @Override
    public String toString()
    {
        return "ProcessControlBlock{" + "acc=" + acc + ", x=" + x + ", pc=" + pc + ", base=" + base + ", limit=" + limit + ", status=" + status + '}';
    }

    public int getBase()
    {
        return base;
    }

    public int getLimit()
    {
        return limit;
    }

    public void setBase(int base)
    {
        this.base = base;
    }

    public void setLimit(int limit)
    {
        this.limit = limit;
    }

    @Override
    public int compareTo(Object compareBlock)
    {
        int compareBases = ((ProcessControlBlock)compareBlock).getBase();
        return this.base - compareBases;
    }

    
    
}
