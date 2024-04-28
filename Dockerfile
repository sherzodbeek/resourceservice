FROM postgres:latest
ENV POSTGRES_USER resource_service
ENV POSTGRES_PASSWORD root123
ENV POSTGRES_DB resource
COPY init.sql /docker-entrypoint-initdb.d/