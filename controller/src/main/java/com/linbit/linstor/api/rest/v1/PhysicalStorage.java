package com.linbit.linstor.api.rest.v1;

import com.linbit.linstor.api.rest.v1.serializer.JsonGenTypes;
import com.linbit.linstor.core.apicallhandler.controller.CtrlPhysicalStorageApiCallHandler;

import javax.inject.Inject;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.glassfish.grizzly.http.server.Request;
import reactor.core.publisher.Mono;

@Path("physical-storage")
@Produces(MediaType.APPLICATION_JSON)
public class PhysicalStorage
{
    private final RequestHelper requestHelper;
    private final CtrlPhysicalStorageApiCallHandler physicalStorageApiCallHandler;
    private final ObjectMapper objectMapper;

    @Inject
    public PhysicalStorage(
        RequestHelper requestHelperRef,
        CtrlPhysicalStorageApiCallHandler ctrlPhysicalStorageApiCallHandler
    )
    {
        requestHelper = requestHelperRef;
        physicalStorageApiCallHandler = ctrlPhysicalStorageApiCallHandler;
        objectMapper = new ObjectMapper();
    }

    @GET
    public void listPhysicalStorage(
        @Context Request request,
        @Suspended AsyncResponse asyncResponse,
        @DefaultValue("0") @QueryParam("limit") int limit,
        @DefaultValue("0") @QueryParam("offset") int offset
    )
    {
        RequestHelper.safeAsyncResponse(asyncResponse, () ->
        {
            Mono<Response> answer = physicalStorageApiCallHandler.listPhysicalStorage()
                .subscriberContext(requestHelper.createContext("ListPhysicalStorage", request))
                .flatMap(physicalStorageMap ->
                {
                    Response resp;
                    final List<JsonGenTypes.PhysicalStorage> physicalStorages =
                        CtrlPhysicalStorageApiCallHandler.groupPhysicalStorageByDevice(physicalStorageMap);

                    Stream<JsonGenTypes.PhysicalStorage> physicalStorageStream = physicalStorages.stream();
                    if (limit > 0)
                    {
                        physicalStorageStream = physicalStorages.stream().skip(offset).limit(limit);
                    }

                    try
                    {
                        resp = Response
                            .status(Response.Status.OK)
                            .entity(objectMapper.writeValueAsString(physicalStorageStream.collect(Collectors.toList())))
                            .build();
                    }
                    catch (JsonProcessingException exc)
                    {
                        exc.printStackTrace();
                        resp = Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
                    }
                    return Mono.just(resp);
                }).next();

            requestHelper.doFlux(asyncResponse, answer);
        });
    }
}
