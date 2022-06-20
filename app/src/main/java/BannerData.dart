/// desc : "我们支持订阅啦~"
/// id : 30
/// imagePath : "https://www.wanandroid.com/blogimgs/42da12d8-de56-4439-b40c-eab66c227a4b.png"
/// isVisible : 1
/// order : 0
/// title : "我们支持订阅啦~"
/// type : 0
/// url : "https://www.wanandroid.com/blog/show/3352"

class BannerData {
  BannerData({
      String? desc, 
      int? id, 
      String? imagePath, 
      int? isVisible, 
      int? order, 
      String? title, 
      int? type, 
      String? url,}){
    _desc = desc;
    _id = id;
    _imagePath = imagePath;
    _isVisible = isVisible;
    _order = order;
    _title = title;
    _type = type;
    _url = url;
}

  BannerData.fromJson(dynamic json) {
    _desc = json['desc'];
    _id = json['id'];
    _imagePath = json['imagePath'];
    _isVisible = json['isVisible'];
    _order = json['order'];
    _title = json['title'];
    _type = json['type'];
    _url = json['url'];
  }
  String? _desc;
  int? _id;
  String? _imagePath;
  int? _isVisible;
  int? _order;
  String? _title;
  int? _type;
  String? _url;
BannerData copyWith({  String? desc,
  int? id,
  String? imagePath,
  int? isVisible,
  int? order,
  String? title,
  int? type,
  String? url,
}) => BannerData(  desc: desc ?? _desc,
  id: id ?? _id,
  imagePath: imagePath ?? _imagePath,
  isVisible: isVisible ?? _isVisible,
  order: order ?? _order,
  title: title ?? _title,
  type: type ?? _type,
  url: url ?? _url,
);
  String? get desc => _desc;
  int? get id => _id;
  String? get imagePath => _imagePath;
  int? get isVisible => _isVisible;
  int? get order => _order;
  String? get title => _title;
  int? get type => _type;
  String? get url => _url;

  Map<String, dynamic> toJson() {
    final map = <String, dynamic>{};
    map['desc'] = _desc;
    map['id'] = _id;
    map['imagePath'] = _imagePath;
    map['isVisible'] = _isVisible;
    map['order'] = _order;
    map['title'] = _title;
    map['type'] = _type;
    map['url'] = _url;
    return map;
  }

}