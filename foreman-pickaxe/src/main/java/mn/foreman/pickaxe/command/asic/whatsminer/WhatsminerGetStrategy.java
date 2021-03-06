package mn.foreman.pickaxe.command.asic.whatsminer;

import mn.foreman.api.ForemanApi;
import mn.foreman.model.command.CommandDone;
import mn.foreman.model.command.CommandStart;
import mn.foreman.model.command.DoneStatus;
import mn.foreman.model.error.MinerException;
import mn.foreman.pickaxe.command.CommandStrategy;
import mn.foreman.whatsminer.WhatsminerQuery;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

import static mn.foreman.pickaxe.command.util.CommandUtils.safeGet;

/** Performs a whatsminer GET. */
public class WhatsminerGetStrategy
        implements CommandStrategy {

    @Override
    public void runCommand(
            final CommandStart command,
            final ForemanApi foremanApi,
            final CommandDone.CommandDoneBuilder builder,
            final Callback callback) {
        final Map<String, Object> args = command.args;

        final String ip =
                safeGet(
                        args,
                        "ip");
        final int port =
                Integer.parseInt(
                        safeGet(
                                args,
                                "port"));
        final String username =
                args.getOrDefault("username", "").toString();
        final String password =
                args.getOrDefault("password", "").toString();
        final String uri =
                safeGet(
                        args,
                        "uri");

        final AtomicInteger code = new AtomicInteger();
        final AtomicReference<String> data = new AtomicReference<>();
        try {
            WhatsminerQuery.query(
                    ip,
                    port,
                    username,
                    password,
                    Collections.singletonList(
                            WhatsminerQuery.Query
                                    .builder()
                                    .uri(uri)
                                    .isGet(true)
                                    .isMultipartForm(false)
                                    .urlParams(Collections.emptyList())
                                    .callback((integer, s) -> {
                                        code.set(integer);
                                        data.set(s);
                                    })
                                    .build()));
        } catch (final MinerException e) {
            data.set(ExceptionUtils.getStackTrace(e));
        }

        callback.done(
                builder
                        .result(
                                ImmutableMap.of(
                                        "code",
                                        code.get(),
                                        "data",
                                        data.get()))
                        .status(
                                CommandDone.Status
                                        .builder()
                                        .type(DoneStatus.SUCCESS)
                                        .build())
                        .build());
    }
}
