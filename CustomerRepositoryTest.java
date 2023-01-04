package com.voltup.customer.network.respository;

import android.app.Application;

import com.voltup.customer.network.viewmodel.CustomerViewModel;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class CustomerRepositoryTest extends TestCase {

    public   CustomerRepository customerRepository;

    @Mock
    private Application mContext;

    @Before
    public void setupRepository() {
        // Mockito has a very convenient way to inject mocks by using the @Mock annotation. To
        // inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.openMocks(this);
        // Get a reference to the class under test
        customerRepository = new CustomerRepository(mContext);

    }

}