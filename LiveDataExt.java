package com.voltup.customer.Utils;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;

import com.voltup.customer.network.model.BaseResponse;
import com.voltup.customer.network.model.prebook.PrebookResponse;
import com.voltup.customer.network.model.prebook.PrebookingConfig;

import org.junit.Test;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import io.reactivex.Single;

public class LiveDataExt<T> {

    public static <T> T getOrAwaitValue(final MutableLiveData<T> mutableLiveData) throws InterruptedException {
        final Object[] data = new Object[1];
        final CountDownLatch latch = new CountDownLatch(1);
        Observer<T> observer = new Observer<T>() {
            @Override
            public void onChanged(@Nullable T o) {
                data[0] = o;
                latch.countDown();
                mutableLiveData.removeObserver(this);
            }
        };
        mutableLiveData.observeForever(observer);
        // Don't wait indefinitely if the LiveData is not set.
        try {
            if (!latch.await(2, TimeUnit.SECONDS)) {
                throw new RuntimeException("LiveData value was never set.");
            }
        } finally {
            mutableLiveData.removeObserver(observer);
        }
        //noinspection unchecked
        return (T) data[0];
    }
}