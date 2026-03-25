import http from 'k6/http';
import {check} from 'k6';
import {Trend, Rate} from 'k6/metrics';

const getTrend = new Trend('GetUsersFiltering');
const getErrorRate = new Rate('GetUsersErrorFiltering');
const names = ['ann', 'john', 'alex', 'kate', 'mike', 'olga'];

function randomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function randomName() {
    return names[randomInt(0, names.length - 1)];
}

function buildQuery(params) {
    return Object.entries(params)
        .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(value)}`)
        .join('&');
}

export let options = {
    stages: [
        {duration: "10s", target: `${__ENV.USERS}`},
        {duration: "100s", target: `${__ENV.USERS}`},
        {duration: "10s", target: 0}
    ]
};

export default function () {
    const sleepMs = Number(__ENV.SLEEP_MS ?? '0');
    const minAge = randomInt(18, 40);
    const maxAge = randomInt(minAge, 80);
    const query = buildQuery({
        // name: randomName(),
        minAge: String(minAge),
        maxAge: String(maxAge),
    });

    const params = {
        headers: {
            'Content-Type': 'application/json',
        },
    };

    const requests = {
        'Get Users': {
            method: 'GET',
            url: `${__ENV.BASE_URL}api/users/filtering?${query}`,
            params: params,
        }
    };

    const responses = http.batch(requests);
    const getResp = responses['Get Users'];

    check(getResp, {
        'status is 200': (r) => r.status === 200,
    }) || getErrorRate.add(1);

    getTrend.add(getResp.timings.duration);

    if (sleepMs > 0) {
        sleep(sleepMs / 1000);
    }
}
