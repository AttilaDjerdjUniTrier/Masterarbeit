from flask import Flask, request, Response, make_response
from flask_restful import Resource, Api

app = Flask(__name__)
api = Api(app)

def bin2int(binstr):
    return int(binstr, 2)

@app.route('/', methods=['GET'])
def index():
    myHeaders = request.headers
    receivedBody = request.data
    receivedBodyDecoded = receivedBody.decode('utf-8')
    print(myHeaders)
    flightAltitudeHeader = myHeaders.get('FlightAlt')
    n = bin2int(flightAltitudeHeader[8:14]+flightAltitudeHeader[15:20])
    qbit = flightAltitudeHeader[15]
    if qbit:
        altitude = n * 25 - 1000
    else:
        altitude = n * 100 - 1000
    responseBody = receivedBodyDecoded + "\nFlughoehe: " + str(altitude)
    resp = make_response(responseBody, 200)
    resp.headers['Content-Type'] = 'text/plain'
    resp.headers['Accept'] = '*/*'
    resp.headers['Accept-Encoding'] = 'gzip, deflate, br'
    resp.headers['connection'] = 'keep-alive'
    if myHeaders.get('twitter'):
        resp.headers['twitter'] = 'true'
    return resp



if __name__ == '__main__':
     app.run(port='9099')