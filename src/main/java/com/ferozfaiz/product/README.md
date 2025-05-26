### Notes on Entity-Attribute-Value (EAV)
I have tested the Entity-Attribute-Value (EAV) pattern with the product catalog, 
and it works well. However, it is not suitable for all use cases. 
It is best suited for scenarios where product attributes are highly variable and change frequently. 
Furthermore, filtering, searching, sorting, and pagination can become complex when working with multiple attributes, 
as this requires left joins for each attribute.

### Class Table Inheritance
Next, I will explore Class Table Inheritance (CTI) for the product catalog. 
CTI is a design pattern that allows you to create a hierarchy of classes, 
where each class can have its own attributes and methods. 
This pattern is useful when you have a fixed set of attributes for each class, 
and you want to avoid the complexity of EAV.