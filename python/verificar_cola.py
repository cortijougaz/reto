from azure.servicebus import ServiceBusClient


CONNECTION_STRING = (
    "Endpoint=sb://localhost;"
    "SharedAccessKeyName=RootManageSharedAccessKey;"
    "SharedAccessKey=SAS_KEY_VALUE;"
    "UseDevelopmentEmulator=true;"
)
QUEUE_NAME = "cola-auditoria-cliente"


def main() -> None:
    with ServiceBusClient.from_connection_string(CONNECTION_STRING) as client:
        receiver = client.get_queue_receiver(queue_name=QUEUE_NAME)

        with receiver:
            messages = receiver.peek_messages(max_message_count=20)

            if not messages:
                print("No existen mensajes activos en la cola.")
                return

            for message in messages:
                properties = message.application_properties or {}
                trace_id = properties.get("traceId") or properties.get(b"traceId")

                print("=" * 60)
                print("Sequence number:", message.sequence_number)
                print("Message ID:", message.message_id)
                print("Correlation ID:", message.correlation_id)
                print("Trace ID:", trace_id)
                print("Payload:", str(message))


if __name__ == "__main__":
    main()
