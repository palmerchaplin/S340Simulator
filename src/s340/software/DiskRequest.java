package s340.software;


public class DiskRequest extends s340.software.os.IORequest {

    private int start;
    private int length;
    

    public DiskRequest(int programIndex, int operation, int start, int length) {
        super(programIndex, operation);
        this.start = start;
        this.length = length;
        
    }

    public int getStart() {
        return start;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getLength() {
        return length;
    }

    public void setLength(int length) {
        this.length = length;
    }

    @Override
    public String toString() {
        return "DiskRequest{" + "start=" + start + ", length=" + length + '}';
    }
    
    

}
