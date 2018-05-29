package in.org.klp.ilpkonnect.SchoolDataPojo;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Properties {

@SerializedName("id")
@Expose
private Integer id;
@SerializedName("name")
@Expose
private String name;
@SerializedName("address")
@Expose
private String address;
@SerializedName("boundary")
@Expose
private Boundary boundary;
@SerializedName("admin1")
@Expose
private String admin1;
@SerializedName("admin2")
@Expose
private String admin2;
@SerializedName("admin3")
@Expose
private String admin3;
@SerializedName("type")
@Expose
private Type type;
@SerializedName("category")
@Expose
private Integer category;
@SerializedName("dise")
@Expose
private Integer dise;
@SerializedName("dise_code")
@Expose
private Long diseCode;
@SerializedName("area")
@Expose
private Object area;
@SerializedName("landmark")
@Expose
private Object landmark;
@SerializedName("pincode")
@Expose
private Integer pincode;
@SerializedName("gender")
@Expose
private String gender;
@SerializedName("management")
@Expose
private Integer management;
@SerializedName("moi")
@Expose
private Object moi;
@SerializedName("sex")
@Expose
private String sex;
@SerializedName("identifiers")
@Expose
private Object identifiers;
@SerializedName("parliament")
@Expose
private Object parliament;
@SerializedName("assembly")
@Expose
private Object assembly;
@SerializedName("ward")
@Expose
private Object ward;
@SerializedName("num_boys")
@Expose
private Object numBoys;
@SerializedName("num_girls")
@Expose
private Object numGirls;
@SerializedName("last_verified_year")
@Expose
private Object lastVerifiedYear;
@SerializedName("institution_languages")
@Expose
private List<String> institutionLanguages = null;

public Integer getId() {
return id;
}

public void setId(Integer id) {
this.id = id;
}

public String getName() {
return name;
}

public void setName(String name) {
this.name = name;
}

public String getAddress() {
return address;
}

public void setAddress(String address) {
this.address = address;
}

public Boundary getBoundary() {
return boundary;
}

public void setBoundary(Boundary boundary) {
this.boundary = boundary;
}

public String getAdmin1() {
return admin1;
}

public void setAdmin1(String admin1) {
this.admin1 = admin1;
}

public String getAdmin2() {
return admin2;
}

public void setAdmin2(String admin2) {
this.admin2 = admin2;
}

public String getAdmin3() {
return admin3;
}

public void setAdmin3(String admin3) {
this.admin3 = admin3;
}

public Type getType() {
return type;
}

public void setType(Type type) {
this.type = type;
}

public Integer getCategory() {
return category;
}

public void setCategory(Integer category) {
this.category = category;
}

public Integer getDise() {
return dise;
}

public void setDise(Integer dise) {
this.dise = dise;
}

public Long getDiseCode() {
return diseCode;
}

public void setDiseCode(Long diseCode) {
this.diseCode = diseCode;
}

public Object getArea() {
return area;
}

public void setArea(Object area) {
this.area = area;
}

public Object getLandmark() {
return landmark;
}

public void setLandmark(Object landmark) {
this.landmark = landmark;
}

public Integer getPincode() {
return pincode;
}

public void setPincode(Integer pincode) {
this.pincode = pincode;
}

public String getGender() {
return gender;
}

public void setGender(String gender) {
this.gender = gender;
}

public Integer getManagement() {
return management;
}

public void setManagement(Integer management) {
this.management = management;
}

public Object getMoi() {
return moi;
}

public void setMoi(Object moi) {
this.moi = moi;
}

public String getSex() {
return sex;
}

public void setSex(String sex) {
this.sex = sex;
}

public Object getIdentifiers() {
return identifiers;
}

public void setIdentifiers(Object identifiers) {
this.identifiers = identifiers;
}

public Object getParliament() {
return parliament;
}

public void setParliament(Object parliament) {
this.parliament = parliament;
}

public Object getAssembly() {
return assembly;
}

public void setAssembly(Object assembly) {
this.assembly = assembly;
}

public Object getWard() {
return ward;
}

public void setWard(Object ward) {
this.ward = ward;
}

public Object getNumBoys() {
return numBoys;
}

public void setNumBoys(Object numBoys) {
this.numBoys = numBoys;
}

public Object getNumGirls() {
return numGirls;
}

public void setNumGirls(Object numGirls) {
this.numGirls = numGirls;
}

public Object getLastVerifiedYear() {
return lastVerifiedYear;
}

public void setLastVerifiedYear(Object lastVerifiedYear) {
this.lastVerifiedYear = lastVerifiedYear;
}

public List<String> getInstitutionLanguages() {
return institutionLanguages;
}

public void setInstitutionLanguages(List<String> institutionLanguages) {
this.institutionLanguages = institutionLanguages;
}

}