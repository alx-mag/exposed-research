* (Exposed) 
  К связанным сущностям можно обращаться только внутри транзакции даже если они были загружены eagerly. 
  Однако это поведение может быть изменено при помощи `DatabseConfig.keepLoadedReferencesOutOfTransaction = true`. 
  [Doc](https://www.jetbrains.com/help/exposed/dao-relationships.html#eager-loading)
* 