import com.github.rholder.retry.*;
import com.google.common.base.Predicates;
import com.world.util.request.HttpUtil;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

/**
 * Created by suxinjie on 2017/11/17.
 */
public class Test {

    public static void main(String[] args) {
        Retryer<Boolean> retryer = RetryerBuilder
                .<Boolean>newBuilder()
                .retryIfException()
                .retryIfResult(Predicates.equalTo(false))
                .withWaitStrategy(WaitStrategies.fixedWait(10, TimeUnit.SECONDS))
                .withStopStrategy(StopStrategies.stopAfterAttempt(3))
//                .withRetryListener()
                .build();


        try {
            retryer.call(httpGetCall);
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (RetryException e) {
            System.out.println("重试3次失败");
        }


    }


    private static Callable<Boolean> httpGetCall = new Callable<Boolean>() {
        @Override
        public Boolean call() throws Exception {

            //逻辑处理
            //HttpUtil.doGet("https://www.okcoin.com/api/v1/ticker.do?symbol=ltcbtc", null);
            System.out.println("业务请求");

            return false;
        }
    };


}
