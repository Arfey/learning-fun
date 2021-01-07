FROM docker.elastic.co/elasticsearch/elasticsearch-oss:7.5.1

COPY ./plugins/simple_rest_handler /simple_rest_handler

# list of plugins which u wanna to test
RUN /usr/share/elasticsearch/bin/elasticsearch-plugin install file:/simple_rest_handler/build/distributions/simple-handler-plugin-1.0.zip
