
    
import time
import pycurl
import urllib
import json
import oauth2 as oauth
import nltk
import datetime
from nltk.corpus import stopwords
from nltk import wordpunct_tokenize
from sys import stdin
from pymongo import Connection




API_ENDPOINT_URL = 'https://stream.twitter.com/1.1/statuses/filter.json'
USER_AGENT = 'TwitterStream 1.0' # This can be anything really

# You need to replace these with your own values
OAUTH_KEYS = {'consumer_key': 'ur3no3lNZsFOS0PsOmJkQ',
              'consumer_secret': 'tzvPt7GKfrjQSzGapHLdYvcD8QLgUtjKsZhadXtFknc',
              'access_token_key': '324169079-qWzpwhyffnlTncbc0dID2I4qDx6Ao2BBS4QOJnnJ',
              'access_token_secret': 'lzJHEQ7r0OW7UHW351BZF71UxJYfQucd2aJonzspNaY'}

# These values are posted when setting up the connection
POST_PARAMS = {'include_entities': 0,
               'stall_warning': 'true',
               'track': 'iphone,ipad,ipod'}

class TwitterStream:
    def __init__(self, timeout=False):
       
        self.oauth_token = oauth.Token(key=OAUTH_KEYS['access_token_key'], secret=OAUTH_KEYS['access_token_secret'])
        self.oauth_consumer = oauth.Consumer(key=OAUTH_KEYS['consumer_key'], secret=OAUTH_KEYS['consumer_secret'])
        self.conn = None
        self.buffer = ''
        self.timeout = timeout
        self.setup_connection()
        
               
        

    def setup_connection(self):
        """ Create persistant HTTP connection to Streaming API endpoint using cURL.
        """
        if self.conn:
            self.conn.close()
            self.buffer = ''
        self.conn = pycurl.Curl()
        # Restart connection if less than 1 byte/s is received during "timeout" seconds
        if isinstance(self.timeout, int):
            self.conn.setopt(pycurl.LOW_SPEED_LIMIT, 1)
            self.conn.setopt(pycurl.LOW_SPEED_TIME, self.timeout)
        self.conn.setopt(pycurl.URL, API_ENDPOINT_URL)
        self.conn.setopt(pycurl.USERAGENT, USER_AGENT)
        # Using gzip is optional but saves us bandwidth.
        self.conn.setopt(pycurl.ENCODING, 'deflate, gzip')
        self.conn.setopt(pycurl.POST, 1)
        self.conn.setopt(pycurl.POSTFIELDS, urllib.urlencode(POST_PARAMS))
        self.conn.setopt(pycurl.HTTPHEADER, ['Host: stream.twitter.com',
                                             'Authorization: %s' % self.get_oauth_header()])
        # self.handle_tweet is the method that are called when new tweets arrive
        self.conn.setopt(pycurl.WRITEFUNCTION, self.handle_tweet)

    def get_oauth_header(self):
        """ Create and return OAuth header.
        """
        params = {'oauth_version': '1.0',
                  'oauth_nonce': oauth.generate_nonce(),
                  'oauth_timestamp': int(time.time())}
        req = oauth.Request(method='POST', parameters=params, url='%s?%s' % (API_ENDPOINT_URL,
                                                                             urllib.urlencode(POST_PARAMS)))
        req.sign_request(oauth.SignatureMethod_HMAC_SHA1(), self.oauth_consumer, self.oauth_token)
        return req.to_header()['Authorization'].encode('utf-8')

    def start(self):
        """ Start listening to Streaming endpoint.
        Handle exceptions according to Twitter's recommendations.
        """
        backoff_network_error = 0.25
        backoff_http_error = 5
        backoff_rate_limit = 60
        while True:
            self.setup_connection()
            try:
                self.conn.perform()
            except:
                # Network error, use linear back off up to 16 seconds
                print 'Network error: %s' % self.conn.errstr()
                print 'Waiting %s seconds before trying again' % backoff_network_error
                time.sleep(backoff_network_error)
                backoff_network_error = min(backoff_network_error + 1, 16)
                continue
            # HTTP Error
            sc = self.conn.getinfo(pycurl.HTTP_CODE)
            if sc == 420:
                # Rate limit, use exponential back off starting with 1 minute and double each attempt
                print 'Rate limit, waiting %s seconds' % backoff_rate_limit
                time.sleep(backoff_rate_limit)
                backoff_rate_limit *= 2
            else:
                # HTTP error, use exponential back off up to 320 seconds
                print 'HTTP error %s, %s' % (sc, self.conn.errstr())
                print 'Waiting %s seconds' % backoff_http_error
                time.sleep(backoff_http_error)
                backoff_http_error = min(backoff_http_error * 2, 320)

    def handle_tweet(self, data):
        """ This method is called when data is received through Streaming endpoint.
        """
	
        self.buffer += data
        if data.endswith('\r\n') and self.buffer.strip():
            # complete message received
            
            connection = Connection('chiran-Vostro-1520', 27017)
            tweetbase = connection['test_tweetbase']  
            collection = tweetbase['tweet-collection']
                    
            
            message = json.loads(self.buffer)
            self.buffer = ''
            msg = ''
            if message.get('limit'):
                print 'Rate limiting caused us to miss %s tweets' % (message['limit'].get('track'))
            elif message.get('disconnect'):
                raise Exception('Got disconnect: %s' % message['disconnect'].get('reason'))
            elif message.get('warning'):
                print 'Got warning: %s' % message['warning'].get('message')
            elif get_language(message.get('text')) == "english":
            
                tweet = {"user": message.get('user').get('name'),
                        "text": message.get('text'),
                        "time": message.get('user').get('created_at'),
                        "lang": message.get('user').get('lang')}    
          
                
                tweets = tweetbase.tweets
                tweets.insert(tweet)  
                print tweets.count()
                for tweet in tweets.find():
                          # print  tweet.get('lang')               
                           continue
                   # print  'User:%s' % message.get('user').get('name')
                   # print  'Location:%s' % message.get('user').get('location')
                   # print  'Time Stamp:%s' % message.get('user').get('created_at')
                    #print  'Language:%s' % message.get('user').get('lang')
        
def get_language_likelihood(input_text):
        """Return a dictionary of languages and their likelihood of being the
        natural language of the input text
        """
     
        input_text = input_text.lower()
        input_words = wordpunct_tokenize(input_text)
     
        language_likelihood = {}
        total_matches = 0
        for language in stopwords._fileids:
            language_likelihood[language] = len(set(input_words) &
                    set(stopwords.words(language)))
     
        return language_likelihood
     
def get_language(input_text):
        """Return the most likely language of the given text
        """
     
        likelihoods = get_language_likelihood(input_text)
        return sorted(likelihoods, key=likelihoods.get, reverse=True)[0]
    

 
if __name__ == '__main__':
    
    ts = TwitterStream()
    ts.setup_connection()
    ts.start()

