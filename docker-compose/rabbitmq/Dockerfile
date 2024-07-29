FROM rabbitmq:3.13.3-alpine

# Define environment variables.
ENV RABBITMQ_USER rabbit-user
ENV RABBITMQ_PASSWORD root123
ENV RABBITMQ_PID_FILE /var/lib/rabbitmq/mnesia/rabbitmq

ADD init.sh /init.sh
RUN chmod +x /init.sh
EXPOSE 15672
EXPOSE 5672

# Define default command
CMD ["/init.sh"]