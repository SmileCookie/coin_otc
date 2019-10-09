package com.world.model.entity.backcapital;

/**
 * <p>@Description: </p>
 *
 * @author buxianguan
 * @date 2018/3/22上午12:33
 */
public class CountDownInfo {
    private long frequency;
    private long countDown;

    public CountDownInfo(long frequency, long countDown){
        this.frequency = frequency;
        this.countDown = countDown;
    }
    public long getFrequency() {
        return frequency;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }

    public long getCountDown() {
        return countDown;
    }

    public void setCountDown(long countDown) {
        this.countDown = countDown;
    }
}
