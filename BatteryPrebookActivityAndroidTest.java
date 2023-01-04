package com.voltup.customer.ui.activity.newui.prebook;

import static com.google.common.truth.Truth.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import org.mockito.Mock;
import org.mockito.Mockito;

@RunWith(JUnit4.class)
public class BatteryPrebookActivityAndroidTest {

    @Test
    public void checkValidation(){
        assertThat(BatteryPrebookActivity.trueReturnning(2)).isTrue();
    }

}