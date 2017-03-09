# Hit Me

Just a mock of a chat application using an api with a static fake data, Caches the DB locallay using Realm

<img src="/shots/screen1.png" width="400">
<img src="/shots/screen2.png" width="400">




|                                                     FEATURES                                                       |
| ------------------------------------------------------------------------------------------------------------------ |
|There should be a view with a conversations between the user and a "fake" friend                                    |
| Messages from the endpoint: http://hitme-dev.us-west-2.elasticbeanstalk.com/api/all-messages should be included    |
| The user should be able to write a message and have it displayed on a screen.                                      |
| When the user posts a message, the app should reply within 5 seconds.                                              |
| The app should support conversations with multiple friends                                                         |
| The app should persist (save) the conversations to disk so they can be retrieved when the app is restarted.        |


API Response sample
-------------------
|{
    "error": null,
    "statusCode": 200,
    "payload": [{
        "sender": "Joey McClane",
        "messages": [{
            "sender": "Joey McClane",
            "message": "Guess what happened to me?",
            "time_sent": 1478592000
        }, {
            "sender": "user",
            "message": "OMG The most amazing thing happened today!",
            "time_sent": 1478592030
        }, {
            "sender": "Joey McClane",
            "message": "I got a concussion",
            "time_sent": 1478592040
        }, {
            "sender": "user",
            "message": "What?! How?",
            "time_sent": 1478592050
        }, {
            "sender": "Joey McClane",
            "message": "A flower pot dropped on my head when I came to see you. What were you going to say?",
            "time_sent": 1478592090
        }, {
            "sender": "user",
            "message": "I was going to say one of my mother's flower pots dropped on some guys head...",
            "time_sent": 1478592110
        }]
    }, {
        "sender": "Dad",
        "messages": [{
            "sender": "user",
            "message": "How do I get the girls to like me?",
            "time_sent": 1478590120
        }, {
            "sender": "Dad",
            "message": "Dump a bucket of glitter on yourself and stand in the sunlight. They'll come running!",
            "time_sent": 1478590220
        }, {
            "sender": "user",
            "message": "Wtf. Dad. no",
            "time_sent": 1478590259
        }, {
            "sender": "Dad",
            "message": "Then grow a pair and talk to them.",
            "time_sent": 1478590350
        }]
    }, {
        "sender": "Carol Popovich",
        "messages": [{
            "sender": "user",
            "message": "Hey Carol. I'm in detention.",
            "time_sent": 1478572000
        }, {
            "sender": "Carol Popovich",
            "message": "What?! Why?",
            "time_sent": 1478572302
        }, {
            "sender": "user",
            "message": "In class my teacher pointed a ruler at me, and said, 'At the other end of this ruler is an idiot.'",
            "time_sent": 1478572774
        }, {
            "sender": "Carol Popovich",
            "message": "And?",
            "time_sent": 1478572929
        }, {
            "sender": "user",
            "message": "I asked him which end he was referring to.",
            "time_sent": 1478573501
        }]
    }]
}|

License
----

MIT


