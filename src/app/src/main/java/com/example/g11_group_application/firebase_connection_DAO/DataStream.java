package com.example.g11_group_application.firebase_connection_DAO;

import java.util.concurrent.TimeUnit;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;


/**
 * Manages a stream of data updates at regular intervals using RxJava.
 * This class is designed to periodically execute data fetching operations
 * and handle the lifecycle of these operations to prevent memory leaks.
 * Utilizes {@link RealTimeFirebaseOperations} for actual data fetching tasks.
 *
 * @Author: Onam Dumbare (u7704695)
 * @Editted: Saksham Gupta (u7726995)
 * Created: 08-May-2024
 * Comments: DataStream class for handling real-time updates with Firebase integration.
 */
public class DataStream {
    private final RealTimeFirebaseOperations firebaseOperations;
    private final long period;
    private final TimeUnit timeUnit;
    private final CompositeDisposable disposables = new CompositeDisposable();

    public DataStream(RealTimeFirebaseOperations firebaseOperations, long period, TimeUnit timeUnit) {
        this.firebaseOperations = firebaseOperations;
        this.period = period;
        this.timeUnit = timeUnit;
    }

    /**
     * Starts the data fetching process at the specified interval.
     * The data fetched is passed to a callback method provided by the caller.
     *
     * @param callback A callback to handle the data received or any errors.
     */
    public void start(RealTimeFirebaseOperations.UserDataListCallback callback) {
        Disposable disposable = Observable.interval(period, timeUnit)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(aLong -> firebaseOperations.getDataList(callback));
        disposables.add(disposable);
    }

    /**
     * Stops the ongoing data fetching process and disposes all associated resources.
     * This should be called to prevent memory leaks when the data stream is no longer needed.
     */
    public void stop() {
        if (!disposables.isDisposed()) {
            disposables.dispose();
        }
    }
}