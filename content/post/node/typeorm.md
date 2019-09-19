---
title: typeorm
date: 2019-08-18 12:00:00
---

# 使用 `REPLACE INTO`
```js
let query = getConnection('ubs_spider_black').createQueryBuilder()
    .insert()
    .into(FrontWebPageRecord)
    .values(records);
let [sql, param] = query.getQueryAndParameters();
sql = sql.replace('INSERT INTO', 'REPLACE INTO');
return await conn.blacklist.manager.query(sql, param);
```

# 使用流式查询
```js
 const  queryRunner = typeorm.createQueryRunner();
await queryRunner.connect();
let stream = await queryRunner.stream('select * from admin');
stream.on('result', result => {
    console.log(result);
})
stream.on('error', err => {
    queryRunner.release();
    console.log(err);
})
stream.on('end', ()=> {
    queryRunner.release();
    stream.close();
})
```

# 批量插入
```js
typeorm.manager
    .query('INSERT INTO admin (version, log_level, mode) VALUES ?', [
        [[1, 2, 3], [2, 3, 4]]
    ])
    .then(res => {});
```