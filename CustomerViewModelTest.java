package com.voltup.customer.network.viewmodel;


import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.voltup.customer.Utils.LiveDataExt;
import com.voltup.customer.network.model.BaseResponse;
import com.voltup.customer.network.model.prebook.PrebookResponse;
import com.voltup.customer.network.model.prebook.PrebookingConfig;
import com.voltup.customer.network.respository.CustomerRepository;

import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import java.util.ArrayList;

import io.reactivex.Single;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.schedulers.Schedulers;

public class CustomerViewModelTest {

    @Spy
    @InjectMocks
    private CustomerViewModel customerViewModel;

    @Mock
    CustomerRepository customerRepository;

    @Mock
    private Application mContext;

    // Executes each task synchronously using Architecture Components.
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Before
    public void setupTasksViewModel() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(schedulerCallable -> Schedulers.trampoline());
        MockitoAnnotations.openMocks(this);

        // Get a reference to the class under test
//        customerViewModel = new CustomerViewModel(customerRepository);
    }

    @AfterClass
    public static void afterClass() throws Exception {
        RxAndroidPlugins.reset();
    }


    @Test
    public void fetchTimeSlot() throws InterruptedException {


        BaseResponse<PrebookResponse> baseResponse = new BaseResponse<>();
        baseResponse.setSuccess(true);
        PrebookResponse prebookResponse = new PrebookResponse();
        ArrayList<PrebookingConfig> prebookingConfigs = new ArrayList<>();
        prebookingConfigs.add(new PrebookingConfig(15,311L));
        prebookResponse.setPrebookingConfigs(prebookingConfigs);
        baseResponse.setData(prebookResponse);
        when(customerRepository.getTimeSlotConfig()).thenReturn(Single.just(baseResponse));
        customerViewModel.getTimeSlotConfig(true);

        assertThat(LiveDataExt.getOrAwaitValue(customerViewModel.timeslotConfigLiveData).getContentIfNotHandled()).isEqualTo(prebookingConfigs);
    }

    @Test
    public void SuccessFalseTimeSlot() throws InterruptedException {

        BaseResponse<PrebookResponse> baseResponse = new BaseResponse<>();
        baseResponse.setSuccess(false);
        baseResponse.setMessage("can't fetch");
        baseResponse.setData(null);
        when(customerRepository.getTimeSlotConfig()).thenReturn(Single.just(baseResponse));
        customerViewModel.getTimeSlotConfig(true);

        assertThat(LiveDataExt.getOrAwaitValue(customerViewModel.errorLiveData)).isEqualTo("can't fetch");
    }

    @Test
    public void NetworkExceptionTimeSlot() {
        when(customerRepository.getTimeSlotConfig()).thenReturn(Single.error(new Exception()));
        customerViewModel.getTimeSlotConfig(true);
        Single<BaseResponse<PrebookResponse>> baseResponseSingle = customerRepository.getTimeSlotConfig();
        baseResponseSingle.test().assertError(Exception.class);
    }



}