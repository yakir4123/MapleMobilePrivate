package messaging;

import static io.grpc.MethodDescriptor.generateFullMethodName;
import static io.grpc.stub.ClientCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ClientCalls.asyncClientStreamingCall;
import static io.grpc.stub.ClientCalls.asyncServerStreamingCall;
import static io.grpc.stub.ClientCalls.asyncUnaryCall;
import static io.grpc.stub.ClientCalls.blockingServerStreamingCall;
import static io.grpc.stub.ClientCalls.blockingUnaryCall;
import static io.grpc.stub.ClientCalls.futureUnaryCall;
import static io.grpc.stub.ServerCalls.asyncBidiStreamingCall;
import static io.grpc.stub.ServerCalls.asyncClientStreamingCall;
import static io.grpc.stub.ServerCalls.asyncServerStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnaryCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedStreamingCall;
import static io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.32.1)",
    comments = "Source: service.proto")
public final class MapleServiceGrpc {

  private MapleServiceGrpc() {}

  public static final String SERVICE_NAME = "messaging.MapleService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<messaging.Service.RequestEvent,
      messaging.Service.ResponseEvent> getEventsStreamMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "EventsStream",
      requestType = messaging.Service.RequestEvent.class,
      responseType = messaging.Service.ResponseEvent.class,
      methodType = io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
  public static io.grpc.MethodDescriptor<messaging.Service.RequestEvent,
      messaging.Service.ResponseEvent> getEventsStreamMethod() {
    io.grpc.MethodDescriptor<messaging.Service.RequestEvent, messaging.Service.ResponseEvent> getEventsStreamMethod;
    if ((getEventsStreamMethod = MapleServiceGrpc.getEventsStreamMethod) == null) {
      synchronized (MapleServiceGrpc.class) {
        if ((getEventsStreamMethod = MapleServiceGrpc.getEventsStreamMethod) == null) {
          MapleServiceGrpc.getEventsStreamMethod = getEventsStreamMethod =
              io.grpc.MethodDescriptor.<messaging.Service.RequestEvent, messaging.Service.ResponseEvent>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.BIDI_STREAMING)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "EventsStream"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  messaging.Service.RequestEvent.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.lite.ProtoLiteUtils.marshaller(
                  messaging.Service.ResponseEvent.getDefaultInstance()))
              .build();
        }
      }
    }
    return getEventsStreamMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static MapleServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MapleServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MapleServiceStub>() {
        @java.lang.Override
        public MapleServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MapleServiceStub(channel, callOptions);
        }
      };
    return MapleServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static MapleServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MapleServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MapleServiceBlockingStub>() {
        @java.lang.Override
        public MapleServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MapleServiceBlockingStub(channel, callOptions);
        }
      };
    return MapleServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static MapleServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<MapleServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<MapleServiceFutureStub>() {
        @java.lang.Override
        public MapleServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new MapleServiceFutureStub(channel, callOptions);
        }
      };
    return MapleServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public static abstract class MapleServiceImplBase implements io.grpc.BindableService {

    /**
     */
    public io.grpc.stub.StreamObserver<messaging.Service.RequestEvent> eventsStream(
        io.grpc.stub.StreamObserver<messaging.Service.ResponseEvent> responseObserver) {
      return asyncUnimplementedStreamingCall(getEventsStreamMethod(), responseObserver);
    }

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
          .addMethod(
            getEventsStreamMethod(),
            asyncBidiStreamingCall(
              new MethodHandlers<
                messaging.Service.RequestEvent,
                messaging.Service.ResponseEvent>(
                  this, METHODID_EVENTS_STREAM)))
          .build();
    }
  }

  /**
   */
  public static final class MapleServiceStub extends io.grpc.stub.AbstractAsyncStub<MapleServiceStub> {
    private MapleServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MapleServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MapleServiceStub(channel, callOptions);
    }

    /**
     */
    public io.grpc.stub.StreamObserver<messaging.Service.RequestEvent> eventsStream(
        io.grpc.stub.StreamObserver<messaging.Service.ResponseEvent> responseObserver) {
      return asyncBidiStreamingCall(
          getChannel().newCall(getEventsStreamMethod(), getCallOptions()), responseObserver);
    }
  }

  /**
   */
  public static final class MapleServiceBlockingStub extends io.grpc.stub.AbstractBlockingStub<MapleServiceBlockingStub> {
    private MapleServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MapleServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MapleServiceBlockingStub(channel, callOptions);
    }
  }

  /**
   */
  public static final class MapleServiceFutureStub extends io.grpc.stub.AbstractFutureStub<MapleServiceFutureStub> {
    private MapleServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected MapleServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new MapleServiceFutureStub(channel, callOptions);
    }
  }

  private static final int METHODID_EVENTS_STREAM = 0;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final MapleServiceImplBase serviceImpl;
    private final int methodId;

    MethodHandlers(MapleServiceImplBase serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_EVENTS_STREAM:
          return (io.grpc.stub.StreamObserver<Req>) serviceImpl.eventsStream(
              (io.grpc.stub.StreamObserver<messaging.Service.ResponseEvent>) responseObserver);
        default:
          throw new AssertionError();
      }
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (MapleServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .addMethod(getEventsStreamMethod())
              .build();
        }
      }
    }
    return result;
  }
}
