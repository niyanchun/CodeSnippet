# pip install kafka-python
import gzip
from kafka import KafkaConsumer
from kafka import TopicPartition

consumer = KafkaConsumer(bootstrap_servers='127.0.0.1:9092')
partition = TopicPartition('test-topic', 6)
start = 7391
end = 7395
consumer.assign([partition])
consumer.seek(partition, start)

i=start
for msg in consumer:
    if msg.offset > end:
        break
    else:
        print(msg)