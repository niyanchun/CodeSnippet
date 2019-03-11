from kazoo.client import KazooClient

zk = KazooClient(hosts='10.9.1.13:2181')
zk.start()

zk.ensure_path("/zoo/dog")
# zk.create("/zoo/dog/Husky", value=b"just a test")

if zk.exists("/zoo"):
    children = zk.get_children("/zoo")

print(children)
zk.stop()
