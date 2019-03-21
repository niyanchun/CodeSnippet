# pip install kazoo
from kazoo.client import KazooClient
 
class ZookeeperConn(object):
    def __init__(self):
        self.ZK_ADDRESS = "10.9.1.176:2181"
        self.ZK = KazooClient(self.ZK_ADDRESS)
        self.ZK.start()
 
    def get_data(self):
        print(self.ZK.get("node"))
 
    def create_node(self, node, data):
        self.ZK.create(node, str.encode(data))
 
    def set_data(self, node, value):
        self.ZK.set(node, str.encode(data))
 
 
node = "/logkeeper_middleware/ABC/logkeeper/cluster-2"
fp =  open("/Users/allan/Desktop/logkeeper/logkeeper-config.json", "r")
data = fp.read()
zk = ZookeeperConn()
zk.create_node(node, data)
# zk.set_data(data)

"""json file
{
    "id": "cluster-2",
    "elasticsearch": {
        "hosts": "10.8.4.13:9200",
        "dataAging": {
            "cronExpression": ["0 6 * *", "0 18 * *"]
        }
    },
    "flink": {
        "hosts": "10.8.4.76:8081"
    },
    "kafka": {
        "hosts": "10.8.4.45:9092"
    },
    "zookeeper": {
        "hosts": "10.8.4.14:2181,10.8.4.44:2181,10.8.4.74:2181"
    },
    "filebeat": {
        "hosts": "10.8.4.11:5066"
    }
}
"""