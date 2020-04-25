FROM adoptopenjdk:8u252-b09-jre-hotspot-bionic
MAINTAINER ertorser

ARG HADOOP_VERSION=2.6.0

# TODO: service ssh start

## update system and install required tools
RUN apt-get update -qq && apt-get install -y -qq \
    wget \
    ssh \
    pdsh \
    net-tools # DROP ME

## passwordless ssh
RUN ssh-keygen -q -N "" -t rsa -f /root/.ssh/id_rsa
RUN cp /root/.ssh/id_rsa.pub /root/.ssh/authorized_keys

## hadoop
RUN wget -q https://archive.apache.org/dist/hadoop/common/hadoop-2.6.0/hadoop-2.6.0.tar.gz | tar -xz -C /usr/local/ && \
    cd /usr/local && \
    ln -s ./hadoop-2.6.0 hadoop

ENV HADOOP_PREFIX /usr/local/hadoop
ENV HADOOP_COMMON_HOME /usr/local/hadoop
ENV HADOOP_HDFS_HOME /usr/local/hadoop
ENV HADOOP_MAPRED_HOME /usr/local/hadoop
ENV HADOOP_YARN_HOME /usr/local/hadoop
ENV HADOOP_CONF_DIR /usr/local/hadoop/etc/hadoop
ENV YARN_CONF_DIR $HADOOP_PREFIX/etc/hadoop

