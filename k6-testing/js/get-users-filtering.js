import { baseGetTest, defaultOptions } from './base-get-test.js';

function randomInt(min, max) {
    return Math.floor(Math.random() * (max - min + 1)) + min;
}

function buildQuery(params) {
    return Object.entries(params)
        .map(([key, value]) => `${encodeURIComponent(key)}=${encodeURIComponent(String(value))}`)
        .join('&');
}

export const options = defaultOptions;

export default baseGetTest({
    metricName: 'GetUsersFiltering',
    errorMetricName: 'GetUsersErrorFiltering',
    buildUrl: (baseUrl) => {
        const minAge = randomInt(18, 40);
        const maxAge = randomInt(minAge, 80);
        const query = buildQuery({
            minAge: String(minAge),
            maxAge: String(maxAge),
        });

        return `${baseUrl}/api/users/filtering?${query}`;
    },
});
