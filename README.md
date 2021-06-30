# Job Manager
A Java Dynamic Job Scheduler APP

## How To Start

After executing the following command, the application will start on `localhost:8080`

- Using maven

```bash
> mvnw clean spring-boot:run
```

## Features
**Create Job**  
Endpoint    : `/job-manager/groups/group/jobs`  
Method      : `POST`  
Status      : `201: Created`  
Body        :
```json
{
  "name": "ExpenseManager",
  "subject": "Daily Expense Report",
  "messageBody": "Sample Expense report",
  "to": [
    "syedtariqfiles@example.com"
  ],
  "cc": [
    "tariq.cse.pstu@example.com"
  ],
  "bcc": [
    "syedtariqcse@example.com"
  ],
  "triggers": [
    {
      "name": "ExpenseManager",
      "group": "email",
      "fireTime": "2021-06-28T17:21:25.000"
    }
  ]
}
```
Content-Type: `application/json`

**View Job**  
Endpoint    : `/job-manager/groups/group/jobs/name`  
Method      : `GET`  
Status      : `200: Ok`  
Body        : NULL  
Accept      : `application/json`

**Edit Job**  
Endpoint    : `/job-manager/groups/group/jobs/name`
Method      : `PUT`  
Status      : `204: No Content`  
Body        :
```json
{
  "name": "ExpenseManager",
  "subject": "Daily Fuel Report",
  "messageBody": "Sample Quartz report",
  "to": [
    "syedtariqfiles@example.com",
    "engr.ssmtariq@example.com"
  ],
  "cc": [
    "tariq.cse.pstu@example.com"
  ],
  "bcc": [
    "syedtariqcse@example.com"
  ]
}
```
Content-Type: `application/json`

**Pause Job (Update)**  
Endpoint      : `/job-manager/groups/group/jobs/name/pause`  
Method      : `PATCH`  
Status      : `204: No Content`  
Body        : NULL  
Content-Type: `*/*`

**Resume Job (Update)**  
Endpoint    : `/job-manager/groups/group/jobs/name/resume`  
Method      : `PATCH`  
Status      : `204: No Content`  
Body        : NULL  
Content-Type: `*/*`

**Delete Job**  
Endpoint    : `/job-manager/groups/group/jobs/name`  
Method      : `DELETE`  
Status      : `204: No Content`  
Body        : NULL  
Content-Type: `*/*`
