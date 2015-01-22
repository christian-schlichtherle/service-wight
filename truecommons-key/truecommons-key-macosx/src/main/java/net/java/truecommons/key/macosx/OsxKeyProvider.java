/*
 * Copyright (C) 2005-2015 Schlichtherle IT Services.
 * All rights reserved. Use is subject to license terms.
 */
package net.java.truecommons.key.macosx;

import java.net.URI;
import java.util.Objects;
import javax.annotation.CheckForNull;
import javax.annotation.concurrent.ThreadSafe;
import net.java.truecommons.key.spec.KeyProvider;
import net.java.truecommons.key.spec.UnknownKeyException;
import net.java.truecommons.key.spec.prompting.AbstractPromptingPbeParameters;

/**
 * Interacts with the {@link OsxKeyManager} to persist passwords into Apple's
 * Keychain Services API.
 *
 * @since  TrueCommons 2.2
 * @author Christian Schlichtherle
 */
@ThreadSafe
final class OsxKeyProvider<P extends AbstractPromptingPbeParameters<P, ?>>
implements KeyProvider<P> {

    private final OsxKeyManager<P> manager;
    private final URI resource;
    private final KeyProvider<P> provider;
    private volatile @CheckForNull P param;

    OsxKeyProvider(
            final OsxKeyManager<P> manager,
            final URI resource,
            final KeyProvider<P> provider) {
        assert null != manager;
        assert null != resource;
        assert null != provider;
        this.manager = manager;
        this.resource = resource;
        this.provider = provider;
    }

    @Override
    public P getKeyForWriting() throws UnknownKeyException {
        P op = param;
        if (null == op) op = manager.getKey(resource);
        if (null != op && !op.isChangeRequested()) return op.clone();
        final P np = provider.getKeyForWriting();
        if (!np.equals(op)) manager.setKey(resource, np);
        return param = np;
    }

    @Override
    public P getKeyForReading(final boolean invalid)
    throws UnknownKeyException {
        if (!invalid) {
            P op = param;
            if (null == op) op = manager.getKey(resource);
            if (null != op) return op.clone();
        }
        return provider.getKeyForReading(invalid);
    }

    @Override
    public void setKey(final @CheckForNull P np) {
        final P op = param;
        provider.setKey(np);
        if (!Objects.equals(np, op)) manager.setKey(resource, np);
        param = np;
    }
}
