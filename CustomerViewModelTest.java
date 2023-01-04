package com.voltup.customer.network.viewmodel;


import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.voltup.customer.network.model.BaseResponse;
import com.voltup.customer.network.model.CustomBaseErrorModel;
import com.voltup.customer.network.model.prebook.PrebookResponse;
import com.voltup.customer.network.model.prebook.PrebookingConfig;
import com.voltup.customer.network.respository.CustomerRepository;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Objects;

import io.reactivex.Single;
import io.reactivex.SingleEmitter;
import io.reactivex.SingleOnSubscribe;
import io.reactivex.android.plugins.RxAndroidPlugins;
import io.reactivex.observers.TestObserver;
import io.reactivex.schedulers.Schedulers;
import io.reactivex.subscribers.TestSubscriber;
import okhttp3.Response;
import retrofit2.HttpException;


public class CustomerViewModelTest {

    // Executes each task synchronously using Architecture Components.
    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();
    @Mock
    CustomerRepository customerRepository;
    private CustomerViewModel customerViewModel;
    @Mock
    private Application mContext;

    @BeforeClass
    public static void setUpClass() {

        // Override the default "out of the box" AndroidSchedulers.mainThread() Scheduler
        //
        // This is necessary here because otherwise if the static initialization block in AndroidSchedulers
        // is executed before this, then the Android SDK dependent version will be provided instead.
        //
        // This would cause a java.lang.ExceptionInInitializerError when running the test as a
        // Java JUnit test as any attempt to resolve the default underlying implementation of the
        // AndroidSchedulers.mainThread() will fail as it relies on unavailable Android dependencies.

        // Comment out this line to see the java.lang.ExceptionInInitializerError
        RxAndroidPlugins.setInitMainThreadSchedulerHandler(__ -> Schedulers.trampoline());
    }

    @AfterClass
    public static void tearDownClass() {
        // Not strictly necessary because we can't reset the value set by setInitMainThreadSchedulerHandler,
        // but it doesn't hurt to clean up anyway.
        RxAndroidPlugins.reset();
    }


    @Before
    public void setupTasksViewModel() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.openMocks(this);
        // Get a reference to the class under test
        customerViewModel = new CustomerViewModel(customerRepository);

    }

    @Test
    public void fetchEmptyTimeSlot() {
        BaseResponse<PrebookResponse> baseResponse = new BaseResponse<>();
        baseResponse.setSuccess(true);
        PrebookResponse prebookResponse = new PrebookResponse();
        ArrayList<PrebookingConfig> prebookingConfigs = new ArrayList<>();
        prebookResponse.setPrebookingConfigs(prebookingConfigs);
        baseResponse.setData(prebookResponse);
        when(customerRepository.getTimeSlotConfig()).thenReturn(Single.just(baseResponse));
        customerViewModel.getTimeSlotConfig(true);
        BaseResponse<PrebookResponse> response =
                customerRepository.getTimeSlotConfig().blockingGet();
        assertThat(response.isSuccess()).isTrue();
        assert (response.getData().getPrebookingConfigs().isEmpty());
    }

    @Test
    public void fetchFilledListTimeSlot() {
        BaseResponse<PrebookResponse> baseResponse = new BaseResponse<>();
        baseResponse.setSuccess(true);
        PrebookResponse prebookResponse = new PrebookResponse();
        ArrayList<PrebookingConfig> prebookingConfigs = new ArrayList<>();
        PrebookingConfig prebookingConfig = new PrebookingConfig(1L, 30L);
        prebookingConfigs.add(prebookingConfig);
        prebookResponse.setPrebookingConfigs(prebookingConfigs);
        baseResponse.setData(prebookResponse);
        when(customerRepository.getTimeSlotConfig()).thenReturn(Single.just(baseResponse));
        customerViewModel.getTimeSlotConfig(true);
        BaseResponse<PrebookResponse> response =
                customerRepository.getTimeSlotConfig().blockingGet();
        assertThat(response.isSuccess()).isTrue();
        assert (!response.getData().getPrebookingConfigs().isEmpty());
    }

    @Test
    public void apiSuccessListTimeSlot() {
        BaseResponse<PrebookResponse> baseResponse = new BaseResponse<>();
        baseResponse.setSuccess(true);
        PrebookResponse prebookResponse = new PrebookResponse();
        ArrayList<PrebookingConfig> prebookingConfigs = new ArrayList<>();
        PrebookingConfig prebookingConfig = new PrebookingConfig(1L, 30L);
        prebookingConfigs.add(prebookingConfig);
        prebookResponse.setPrebookingConfigs(prebookingConfigs);
        baseResponse.setData(prebookResponse);
        when(customerRepository.getTimeSlotConfig()).thenReturn(Single.just(baseResponse));
        customerViewModel.getTimeSlotConfig(true);

        assertThat(customerViewModel.progressLiveData.getValue()).isTrue();
        Single<BaseResponse<PrebookResponse>> baseResponseSingle =
                customerRepository.getTimeSlotConfig();

        baseResponseSingle.test().assertComplete().assertNoErrors()
                .assertValue(prebookResponseBaseResponse -> {
                    assertThat(prebookResponseBaseResponse.isSuccess()).isTrue();
                    assert (!prebookResponseBaseResponse.getData().getPrebookingConfigs()
                            .isEmpty());
                    assert (customerViewModel.timeslotConfigLiveData.getValue() != null);
                    assert (customerViewModel.errorLiveData.getValue() == null);
                    assertThat(customerViewModel.progressLiveData.getValue()).isFalse();
                    return true;
                });
    }

    @Test
    public void apiFailureListTimeSlot() {
        CustomBaseErrorModel customBaseErrorModel = new CustomBaseErrorModel();
        customBaseErrorModel.setMessage("Something went wrong!");
        when(customerRepository.getTimeSlotConfig()).thenReturn(Single.error(customBaseErrorModel));
        customerViewModel.getTimeSlotConfig(true);
        assertThat(customerViewModel.progressLiveData.getValue()).isTrue();
        Single<BaseResponse<PrebookResponse>> baseResponseSingle =
                customerRepository.getTimeSlotConfig();
        baseResponseSingle.test().assertError(throwable -> {
            assertThat(customerViewModel.progressLiveData.getValue()).isFalse();
            assert (customerViewModel.errorLiveData.getValue() != null);
            assert Objects.equals(customerViewModel.errorLiveData.getValue(), "Something went wrong!");
            return true;
        });
    }

    @Test
    public void failedResponseListTimeSlot() {
        BaseResponse<PrebookResponse> baseResponse = new BaseResponse<>();
        baseResponse.setSuccess(false);
        when(customerRepository.getTimeSlotConfig()).thenReturn(Single.just(baseResponse));
        customerViewModel.getTimeSlotConfig(true);
        BaseResponse<PrebookResponse> response =
                customerRepository.getTimeSlotConfig().blockingGet();
        assertThat(response.isSuccess()).isFalse();
    }

    @Test
    public void successResponseListTimeSlot() {
        BaseResponse<PrebookResponse> baseResponse = new BaseResponse<>();
        baseResponse.setSuccess(true);
        when(customerRepository.getTimeSlotConfig()).thenReturn(Single.just(baseResponse));
        customerViewModel.getTimeSlotConfig(true);
        BaseResponse<PrebookResponse> response =
                customerRepository.getTimeSlotConfig().blockingGet();
        assertThat(response.isSuccess()).isTrue();
    }

    @After
    public void tearDown() {
    }
}