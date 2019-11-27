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
使用原生的 MySQL 插入
```js
typeorm.manager
    .query('INSERT INTO admin (version, log_level, mode) VALUES ?', [
        [[1, 2, 3], [2, 3, 4]]
    ])
    .then(res => {});
```

# 扩展 typeorm 的装饰器
可以用如下方式使用装饰器:
```js
import {
    ColumnInfo,
    PrimaryGeneratedColumn,
    Table
} from 'configuration/annotation';

@Table({
    name: 'input',
    type: 'front',
    addSearch: false
})
export default class InputRecord {
    @PrimaryGeneratedColumn()
    id: string;

    @ColumnInfo({ name: 'keyword', cnName: '关键词', readOnly: true, add: true, search: true })
    keyword: string;
}
```

`ColumnInfo` 是对 `Column` 的扩展：
```js
// 继承自 typeorm 的 ColumnOptions，在扩展中调用 typeorm 
export interface TableColumn extends ColumnOptions {
    name: string; // 数据库表列名
    cnName?: string; // 中文名
    tsName?: string; // 配置的特殊类使用的属性对应关系
    readOnly?: boolean;
    itemsRemote?: string; // 从远程加载下拉选项
    search?: boolean; // 是否可以搜索
    add?: boolean; // 新增列表
    required?: boolean
}

export function ColumnInfo(info: TableColumn): Function {
    return function(target: Object, propertyName: string) {
        // 调用 typeorm 的 Column 方法
        Column(info)(target, propertyName);
        let construct = target.constructor;
        if (construct) {
            info.tsName = propertyName;
            if (excludeSet.has(info.name)) {
                info.add = info.add || false;
                info.search = info.search || false;
            }
            if (columnMap.has(construct)) {
                columnMap.get(construct).push(info);
            } else {
                columnMap.set(construct, [info]);
            }
        }
    };
}
```