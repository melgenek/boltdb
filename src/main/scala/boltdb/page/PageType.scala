package boltdb.page

enum PageType {
  case Branch, Leaf, Meta, Freelist, Unknown
}
