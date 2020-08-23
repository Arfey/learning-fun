import time
import redis


redis_client = redis.Redis(decode_responses=True)

ARTICLE_ID = 1
USER_ID = 1
TIME_SET = 'time:'
VOIT_SET = 'voit:%s'
SCORE_SET = 'score:'
ARTICLE_HASH = 'article:%s'

# prepare data

redis_client.zadd(TIME_SET, {ARTICLE_ID: time.time()})
redis_client.delete(VOIT_SET)

def article_vote(conn, user_id, article_id):
    time_limit = time.time() - 7 * 86400

    if (conn.zscore(TIME_SET, article_id) or 0) < time_limit:
        return


    if redis_client.sadd(VOIT_SET % article_id, user_id):
        redis_client.zincrby(SCORE_SET, article_id, 432)
        redis_client.hincrby(ARTICLE_HASH % article_id, 'votes', 1)

    print(redis_client.hgetall(ARTICLE_HASH % article_id))
    print(redis_client.zrange(TIME_SET, article_id, 0, -1))


article_vote(redis_client, ARTICLE_ID, USER_ID)

