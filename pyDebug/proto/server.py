from flask import Flask, request, Response
import sqlite3
import user_pb2
import os

app = Flask(__name__)

DB_FILE = "users.db"

def init_db():
    conn = sqlite3.connect(DB_FILE)
    c = conn.cursor()
    c.execute('''
        CREATE TABLE IF NOT EXISTS users (
            id INTEGER PRIMARY KEY AUTOINCREMENT,
            useId INTEGER NOT NULL,
            decStr TEXT NOT NULL,
            friendImageId TEXT NOT NULL,
            timeStr TEXT NOT NULL,
            friendVideoId TEXT NOT NULL,
            friendVideoTime TEXT NOT NULL,
            likeState INTEGER NOT NULL,
            likesId TEXT NOT NULL
        )
    ''')
    conn.commit()
    conn.close()

@app.route('/create_user', methods=['POST'])
def create_user():
    try:
        user = user_pb2.User()
        user.ParseFromString(request.data)
        print(f"收到请求信息 ： {user.useId}")

        conn = sqlite3.connect(DB_FILE)
        c = conn.cursor()
        c.execute("INSERT INTO users (useId, decStr, friendImageId, timeStr, friendVideoId, friendVideoTime, likeState, likesId) VALUES (?, ?, ?, ?, ?, ?, ?, ?)",(user.useId, user.decStr, user.friendImageId, user.timeStr, user.friendVideoId, user.friendVideoTime, user.likeState, user.likesId))
        conn.commit()
        user_id = c.lastrowid
        conn.close()

        uid = user_pb2.UserId(id=user_id)
        return Response(uid.SerializeToString(), mimetype='application/octet-stream')
    except Exception as e:
        print(e)
        return Response("error", status=400)
    
@app.route('/list_users', methods=['POST'])
def list_users():

    try:
        # 解析空消息
        empty = user_pb2.Empty()
        empty.ParseFromString(request.data)
        print(f"收到请求列表信息")
        conn = sqlite3.connect(DB_FILE)
        c = conn.cursor()
        c.execute("SELECT id, useId, decStr, friendImageId, timeStr, friendVideoId, friendVideoTime, likeState, likesId FROM users")
        users = user_pb2.UserList()
        for row in c.fetchall():
            u = users.users.add()
            u.id = row[0]
            u.useId = row[1]
            u.decStr = row[2]
            u.friendImageId = row[3]
            u.timeStr = row[4]
            u.friendVideoId = row[5]
            u.friendVideoTime = row[6]
            u.likeState = row[7]
            u.likesId = row[8]
        conn.close()
        return Response(users.SerializeToString(), mimetype='application/octet-stream')
    except Exception as e:
        print(e)
        return Response("error", status=400)

if __name__ == "__main__":
    init_db()
    app.run(host='0.0.0.0', port=5000, debug=True)