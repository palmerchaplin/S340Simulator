/*
 */
package s340.software;

/**
 *
 * @author palmerchaplin
 */
public class FreeSpace implements Comparable
{
    private int base;
    private int limit;
    
    public FreeSpace(int base, int limit)
    {
        this.base = base;
        this. limit = limit;
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
    public int compareTo(Object compareSpace) {
        int compareBases = ((FreeSpace) compareSpace).getBase();
        return this.base - compareBases;
    }
    
    public String toString(){
        return "Base: " + this.base + " Limit: " + limit;
    }

}
