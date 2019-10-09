//package com.tenstar.timer.auto.AutoBrushAmount;
//
//import io.reactivex.Observable;
//import io.reactivex.ObservableEmitter;
//import io.reactivex.ObservableOnSubscribe;
//import io.reactivex.Observer;
//import io.reactivex.disposables.Disposable;
//
///**
// * Created by renfei on 17/5/24.
// *
// * TODO 刷量,响应式RxJava
// */
//public class CaesarScheduler {
//
//
//
//    public static void main(String[] args){
////创建一个上游 Observable：
//        Observable.create(new ObservableOnSubscribe<Integer>() {
//            @Override
//            public void subscribe(ObservableEmitter<Integer> emitter) throws Exception {
//                emitter.onNext(1);
//                emitter.onNext(2);
////                emitter.onNext(3);
//                emitter.onComplete();
//            }
//        }).subscribe(new Observer<Integer>() {
//            @Override
//            public void onSubscribe(Disposable d) {
//                log.info("subscribe");
//            }
//
//            @Override
//            public void onNext(Integer value) {
//                log.info("" + value);
//            }
//
//            @Override/**/
//            public void onError(Throwable e) {
//                log.error("error");
//            }
//
//            @Override
//            public void onComplete() {
//                log.info("complete");
//            }
//        });
//    }
//}
