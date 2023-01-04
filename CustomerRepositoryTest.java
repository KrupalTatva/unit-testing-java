package com.voltup.customer.network.repository;

import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.when;

import android.app.Application;

import androidx.arch.core.executor.testing.InstantTaskExecutorRule;

import com.voltup.customer.network.model.BaseResponse;
import com.voltup.customer.network.model.prebook.PrebookResponse;
import com.voltup.customer.network.model.prebook.PrebookingConfig;
import com.voltup.customer.network.respository.CustomerRepository;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;

import io.reactivex.Single;

public class CustomerRepositoryTest {

    @Rule
    public InstantTaskExecutorRule instantExecutorRule = new InstantTaskExecutorRule();

    @Mock
    CustomerRepository customerRepository;
    @Mock
    private Application mContext;

    @Before
    public void setupTasksViewModel() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.openMocks(this);

    }


    @Test public void fetchEmptyTimeSlot () {
        BaseResponse<PrebookResponse> baseResponse = new BaseResponse<>();
        baseResponse.setSuccess(true);
        PrebookResponse prebookResponse = new PrebookResponse();
        ArrayList<PrebookingConfig> prebookingConfigs = new ArrayList<>();
        prebookResponse.setPrebookingConfigs(prebookingConfigs);
        baseResponse.setData(prebookResponse);
        when(customerRepository.getTimeSlotConfig()).thenReturn(Single.just(baseResponse));
        BaseResponse<PrebookResponse> response =
                customerRepository.getTimeSlotConfig().blockingGet();
        assertThat(response.isSuccess()).isTrue();
        assertThat(baseResponse).isEqualTo(response);
    }




}
