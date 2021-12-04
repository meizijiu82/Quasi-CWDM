package general;

public class Common {

    String name;
    int index;

    public Common(String name,int index) {
        this.index=index;
        this.name=name;
    }

    /***************setters and getters******************/
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

}
