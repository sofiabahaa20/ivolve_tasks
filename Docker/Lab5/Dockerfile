FROM node:22-alpine

# Update npm to pick up patched bundled dependencies
RUN npm install -g npm@latest

WORKDIR /app

COPY package.json ./

RUN npm install --omit=dev

COPY app.js ./

EXPOSE 3000

CMD ["npm", "start"]
