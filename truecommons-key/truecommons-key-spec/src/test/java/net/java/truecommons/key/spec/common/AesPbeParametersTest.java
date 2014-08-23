/*
 * Copyright (C) 2005-2013 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truecommons.key.spec.common;

import net.java.truecommons.key.spec.common.AesPbeParameters;
import net.java.truecommons.key.spec.common.AesKeyStrength;
import net.java.truecommons.key.spec.prompting.PromptingPbeParametersTestSuite;

/**
 * @author Christian Schlichtherle
 */
public class AesPbeParametersTest
extends PromptingPbeParametersTestSuite<AesPbeParameters, AesKeyStrength> {

    @Override
    protected AesPbeParameters newParam() { return new AesPbeParameters(); }
}
