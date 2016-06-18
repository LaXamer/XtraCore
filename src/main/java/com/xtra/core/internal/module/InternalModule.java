/**
 * This file is part of XtraCore, licensed under the MIT License (MIT).
 *
 * Copyright (c) 2016 - 2016 XtraStudio <https://github.com/XtraStudio>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.xtra.core.internal.module;

import com.xtra.core.internal.Internals;
import com.xtra.core.util.exceptions.XtraCoreException;
import com.xtra.core.util.log.Logger;
import com.xtra.core.util.log.Logger.Level;

/**
 * Similarities between XtraCore 'modules'. Note that they are not referred to
 * as modules in the code-base (save for this class) and do not quite act like
 * 'modules'. This class is purely to centralize common methods.
 */
public class InternalModule {

    /**
     * Checks if Core has been initialized. If not, then everything will fail!
     */
    protected void checkHasCoreInitialized() {
        if (!Internals.initialized) {
            Internals.logger = new Logger(true);
            Internals.logger.log(Level.ERROR,
                    new XtraCoreException("com.xtra.core.Core has not been initialized yet! XtraCore cannot function properly!"));
        }
    }
}
