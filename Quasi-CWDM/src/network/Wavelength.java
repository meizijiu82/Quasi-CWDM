package network;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;


public class Wavelength {

    private int waveTotalNumbers = 80;                              //可用波长总数
    private LinkedList<Integer> avaWavelist=new LinkedList<>();     //可用波长集合
    private LinkedList<Integer> wavelengthList=null;                //已占用波长集合
    private LinkedList<Integer> totUsedList=new LinkedList<>();     //总使用波长集合



/********************  getters and setters  *******************************/

    public LinkedList<Integer> getTotUsedList() {
        return totUsedList;
    }

    public void setTotUsedList(LinkedList<Integer> totUsedList) {
        this.totUsedList = totUsedList;
    }

    public int getWaveTotalNumbers() {
        return waveTotalNumbers;
    }

    public void setWaveTotalNumbers(int waveTotalNumbers) {
        this.waveTotalNumbers = waveTotalNumbers;
    }

    public LinkedList<Integer> getAvaWavelist() {
        return avaWavelist;
    }

    public void setAvaWavelist(LinkedList<Integer> avaWavelist) {
        this.avaWavelist = avaWavelist;
    }

    public LinkedList<Integer> getWavelengthList() {
        return wavelengthList;
    }

    public void setWavelengthList(LinkedList<Integer> wavelengthList) {
        this.wavelengthList = wavelengthList;
    }
}
