import http from 'k6/http';
import { check, sleep } from 'k6';
import { Trend, Rate } from 'k6/metrics';

const getTrend = new Trend('GetUsers');
const getErrorRate = new Rate('GetUsersError');

export let options = {
    stages: [
        { duration: "10s", target: `${__ENV.USERS}` },
        { duration: "100s", target: `${__ENV.USERS}` },
        { duration: "10s", target: 0 }
    ]
};

export default function () {
    const url = __ENV.BASE_URL || `http://${__ENV.HOST || 'localhost'}:${__ENV.PORT}/`;

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const requests = {
        'Get Users': {
            method: 'GET',
            url: url +'api/users',
            params: params,
        }
    };

    const responses = http.batch(requests);
    const getResp = responses['Get Users'];

    check(getResp, {
        'status is 200': (r) => r.status === 200,
    }) || getErrorRate.add(1);

    getTrend.add(getResp.timings.duration);

}
