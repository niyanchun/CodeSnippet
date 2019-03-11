from datetime import datetime
import os
import threading
from apscheduler.schedulers.blocking import BlockingScheduler

def tick():
    print('Tick! The time is: %s' % datetime.now())
    print('Thread Count: %d' % threading.active_count())


if __name__ == '__main__':
    scheduler = BlockingScheduler()
    period = {'second': '*/5' }
    # scheduler.add_job(tick, 'interval', seconds=3)
    scheduler.add_job(tick, 'cron', **period)

    print('Press Ctrl+{0} to exit'.format('Break' if os.name == 'nt' else 'C'))

    try:
        scheduler.start()
    except (KeyboardInterrupt, SystemExit):
        pass