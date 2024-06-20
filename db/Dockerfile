FROM postgres:latest
COPY northwind_init.sql /docker-entrypoint-initdb.d/
EXPOSE 5432
CMD ["postgres"]
