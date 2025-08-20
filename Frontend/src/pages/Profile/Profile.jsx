import { useState } from "react";
import { Badge } from "@/components/ui/badge";
import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import {
  Dialog,
  DialogContent,
  DialogHeader,
  DialogTitle,
  DialogTrigger,
} from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { CheckCircle2, VerifiedIcon } from "lucide-react";
import { useDispatch, useSelector } from "react-redux";
import AccountVarificationForm from "./AccountVarificationForm";
import { useNavigate } from "react-router-dom";
import { enableTwoStepAuthentication, verifyOtp, updateUserProfile } from "@/Redux/Auth/Action";
import {uploadToCloudinary} from "@/Util/uploadToCloudinary";

const Profile = () => {
  const { auth } = useSelector((store) => store);
  const dispatch = useDispatch();

  const [openEdit, setOpenEdit] = useState(false);
  const [successMsg, setSuccessMsg] = useState(false);

  const [formData, setFormData] = useState({
    email: auth.user?.email || "",
    fullName: auth.user?.fullName || "",
    dob: auth.user?.dob || "",
    nationality: auth.user?.nationality || "",
    address: auth.user?.address || "",
    city: auth.user?.city || "",
    postcode: auth.user?.postcode || "",
    country: auth.user?.country || "",
    mobile: auth.user?.mobile || "",
    picture: auth.user?.picture || "",
  });

  const navigate = useNavigate();

  const handleProfileUpdate = (e) => {
    e.preventDefault();
    dispatch(updateUserProfile(localStorage.getItem("jwt"), formData)).then(() => {
      setOpenEdit(false);
      setSuccessMsg(true);
      setTimeout(() => setSuccessMsg(false), 3000);
    });
  };

  const handleEnableTwoStepVerification = (otp) => {
    dispatch(enableTwoStepAuthentication({ jwt: localStorage.getItem("jwt"), otp }));
  };

  const handleVerifyOtp = (otp) => {
    dispatch(verifyOtp({ jwt: localStorage.getItem("jwt"), otp }));
  };

  const handleImageUpload = async (file) => {
    if (!file) return;
    const url = await uploadToCloudinary(file);
    if (url) {
      setFormData({ ...formData, picture: url });
    }
  };

  return (
    <div className="flex flex-col items-center mb-5">
      <div className="pt-10 w-full lg:w-[60%]">
        <Card className="w-full">
          <CardHeader className="pb-9">
            <div className="flex items-center gap-3">
            <CardTitle>Your Information</CardTitle>
            <Dialog open={openEdit} onOpenChange={setOpenEdit}>
              <DialogTrigger asChild>
                <Button variant="outline" size="sm" className="space-x-2 text-black font-bold bg-orange-500">Edit</Button>
              </DialogTrigger>
              <DialogContent className="max-w-3xl w-full">
                <DialogHeader>
                  <DialogTitle>Edit Profile</DialogTitle>
                </DialogHeader>
                <form className="grid grid-cols-2 gap-4" onSubmit={handleProfileUpdate}>
                  {/* Profile Picture Upload */}
                  <div className="col-span-2">
                    <Label>Profile Picture</Label>
                    <Input
                      type="file"
                      accept="image/*"
                      onChange={(e) => handleImageUpload(e.target.files[0])}
                    />
                    {formData.picture && (
                      <img
                        src={formData.picture}
                        alt="Preview"
                        className="mt-2 h-20 w-20 object-cover rounded-full"
                      />
                    )}
                  </div>
                  <div>
                    <Label>Email</Label>
                    <Input value={formData.email} disabled />
                  </div>
                  <div>
                    <Label>Full Name</Label>
                    <Input value={formData.fullName} disabled />
                  </div>
                  <div>
                    <Label>Mobile</Label>
                    <Input
                      value={formData.mobile}
                      onChange={(e) => setFormData({ ...formData, mobile: e.target.value })}
                    />
                  </div>
                  <div>
                    <Label>Date of Birth</Label>
                    <Input
                      type="date"
                      value={formData.dob}
                      onChange={(e) => setFormData({ ...formData, dob: e.target.value })}
                    />
                  </div>
                  <div>
                    <Label>Address</Label>
                    <Input
                      value={formData.address}
                      onChange={(e) => setFormData({ ...formData, address: e.target.value })}
                    />
                  </div>
                  <div>
                    <Label>City</Label>
                    <Input
                      value={formData.city}
                      onChange={(e) => setFormData({ ...formData, city: e.target.value })}
                    />
                  </div>
                  <div>
                    <Label>Postcode</Label>
                    <Input
                      value={formData.postcode}
                      onChange={(e) => setFormData({ ...formData, postcode: e.target.value })}
                    />
                  </div>
                  <div>
                    <Label>Country</Label>
                    <Input
                      value={formData.country}
                      onChange={(e) => setFormData({ ...formData, country: e.target.value })}
                    />
                  </div>
                  <div className="col-span-2">
                    <Button type="submit" className="w-full">Update Profile</Button>
                  </div>
                </form>
              </DialogContent>

            </Dialog>
            </div>
          </CardHeader>
          <CardContent>
            <div className="lg:flex gap-32">
              <div className="space-y-7">
                <div className="flex">
                  <p className="w-[9rem]">Email : </p>
                  <p className="text-gray-500">{auth.user?.email} </p>
                </div>
                <div className="flex">
                  <p className="w-[9rem]">Full Name : </p>
                  <p className="text-gray-500">{auth.user?.fullName} </p>
                </div>
                <div className="flex">
                  <p className="w-[9rem]">Date Of Birth : </p>
                  <p className="text-gray-500">{auth.user?.dob || "N/A"} </p>
                </div>
                <div className="flex">
                  <p className="w-[9rem]">Contact No : </p>
                  <p className="text-gray-500">{auth.user?.mobile || "N/A"} </p>
                </div>
              </div>
              <div className="space-y-7">
                <div className="flex">
                  <p className="w-[9rem]">Address : </p>
                  <p className="text-gray-500">{auth.user?.address || "N/A"} </p>
                </div>
                <div className="flex">
                  <p className="w-[9rem]">City : </p>
                  <p className="text-gray-500">{auth.user?.city || "N/A"} </p>
                </div>
                <div className="flex">
                  <p className="w-[9rem]">Postcode : </p>
                  <p className="text-gray-500">{auth.user?.postcode || "N/A"}</p>
                </div>
                <div className="flex">
                  <p className="w-[9rem]">Country : </p>
                  <p className="text-gray-500">{auth.user?.country || "N/A"}</p>
                </div>
              </div>
            </div>
            {successMsg && (
              <div className="flex items-center gap-2 mt-3 text-green-600">
                <CheckCircle2 size={20} /> Profile updated successfully!
              </div>
            )}
          </CardContent>
        </Card>

        {/* 2 Step Verification */}
        <div className="mt-6">
          <Card className="w-full">
            <CardHeader className="pb-7">
              <div className="flex items-center gap-3">
                <CardTitle>2 Step Verification</CardTitle>
                {auth.user.twoFactorAuth?.enabled ? (
                  <Badge className="space-x-2 text-white bg-green-600">
                    <VerifiedIcon /> <span>{"Enabled"}</span>
                  </Badge>
                ) : (
                  <Badge className="bg-orange-500">Disabled</Badge>
                )}
              </div>
            </CardHeader>
            <CardContent className="space-y-5">
              <Dialog>
                <DialogTrigger>
                  <Button>Enabled Two Step Verification</Button>
                </DialogTrigger>
                <DialogContent>
                  <DialogHeader>
                    <DialogTitle className="px-10 pt-5 text-center">
                      verify your account
                    </DialogTitle>
                  </DialogHeader>
                  <AccountVarificationForm handleSubmit={handleEnableTwoStepVerification} />
                </DialogContent>
              </Dialog>
            </CardContent>
          </Card>
        </div>

        {/* Change Password + Account Status */}
        <div className="lg:flex gap-5 mt-5">
          <Card className="w-full">
            <CardHeader className="pb-7">
              <CardTitle>Change Password</CardTitle>
            </CardHeader>
            <CardContent className="space-y-5">
              <div className="flex items-center">
                <p className="w-[8rem]">Email :</p>
                <p>{auth.user.email}</p>
              </div>
              <div className="flex items-center">
                <p className="w-[8rem]">Password :</p>
                <Button onClick={() => navigate("/forgot-password")} variant="secondary">Change Password</Button>
              </div>
            </CardContent>
          </Card>

          <Card className="w-full">
            <CardHeader className="pb-7">
              <div className="flex items-center gap-3">
                <CardTitle>Account Status</CardTitle>
                {auth.user.verified ? (
                  <Badge className="space-x-2 text-white bg-green-600">
                    <VerifiedIcon /> <span>verified</span>
                  </Badge>
                ) : (
                  <Badge className="bg-orange-500">pending</Badge>
                )}
              </div>
            </CardHeader>
            <CardContent className="space-y-5">
              <div className="flex items-center">
                <p className="w-[8rem]">Email :</p>
                <p>{auth.user.email}</p>
              </div>
              <div className="flex items-center">
                <p className="w-[8rem]">Mobile :</p>
                <p>{auth.user.mobile || "N/A"}</p>
              </div>
              <Dialog>
                <DialogTrigger>
                  <Button>Verify Account</Button>
                </DialogTrigger>
                <DialogContent>
                  <DialogHeader>
                    <DialogTitle className="px-10 pt-5 text-center">
                      verify your account
                    </DialogTitle>
                  </DialogHeader>
                  <AccountVarificationForm handleSubmit={handleVerifyOtp} />
                </DialogContent>
              </Dialog>
            </CardContent>
          </Card>
        </div>
      </div>
    </div>
  );
};

export default Profile;
