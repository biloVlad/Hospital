**json for add:**
```json
{
    "name":"Влад",
    "surename":"Васильевич",
    "lastname":"Бильо",
    "org":"Поликлиника",
    "orgunit":"5",
    "room":"13b",
    "document":{
        "pnum":"846758",
        "pser":"EA"
    },
    "info":{
        "data":{
            "t":"36.6"
        },
        "medication":{
            "doctor":"Корзин Олег Николаевич",
            "name":"Мукалтин",
            "cod":"746df",
            "dosage":{
                "time":"2",
                "count":"1"
            }
        }
    }
}
```

**json for set temperature:**
```json
{
    "pnum" : "111111",
    "pser" : "AA",
    "t":"36.0"
}
```

**json for relocate:**
```json
{
    "pnum" : "111111",
    "pser" : "AA",
    "room":"2a"
}
```