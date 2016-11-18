class Api::Modelv1

	def decrypt_password(password)
		key = "49f9ceb472a7c3d24bb3fd38f6e1647d13c01d2e8ed03a72230c4641dabff0789f89a4cae83823378277138579d945a4733c24903e90201f8c1059947338e2b5"
        crypt = ActiveSupport::MessageEncryptor.new(key)
        return crypt.decrypt_and_verify(password)
	end

	def get_countries(search_params)
		searched_countries = Country.all
		return {:status => searched_countries.count > 0 ? 1 : 0, :items => searched_countries.map { |object| object.to_api}}
	end

	def get_states(search_params)
		searched_states = State.any_of({:name => /^#{search_params[:query]}/i}).and(:country_id => search_params[:country_id])
		return {:status => searched_states.count > 0 ? 1 : 0, :items => searched_states.map { |object| object.to_api}}
	end

	def get_cities(search_params)
		searched_cities = City.any_of({:name => /^#{search_params[:query]}/i}).and(:state_id => search_params[:state_id])
		return {:status => searched_cities.count > 0 ? 1 : 0, :items => searched_cities.map { |object| object.to_api}}
	end

	def check_login_by_facebook(login_params)
		if User.where(:fb_id => login_params[:fb_id], :email => login_params[:email]).any?
			user = User.find_by(:fb_id => login_params[:fb_id], :email => login_params[:email])
			return {:status => 1, :message => "Email and id combination is correct", :api_key => user.api_key, :user_data => user.to_api}
		else
			return {:status => 0, :message => "User with email do not exists"}
		end
	end

	def check_login(login_params)
		if User.where(:email_address => login_params[:email_address]).any?
			user = User.find_by(:email_address => login_params[:email_address])
			if decrypt_password(user.encrypted_password) == login_params[:password]
				return {:status => 1, :message => "Email and password combination is correct", :user_data => user.to_api}
			else
				return {:status => 0, :message => "Email and password combination is incorrect"}
			end
		else
			return {:status => 0, :message => "User with email do not exists"}
		end
	end

	def create_account(account_params)
		user = User.create(:email_address => account_params[:email_address], :first_name => account_params[:first_name], :last_name => account_params[:last_name], :password => account_params[:password].blank? ? "" : account_params[:password], :longitude => account_params[:longitude].blank? ? 0.0 : account_params[:longitude], :latitude => account_params[:latitude].blank? ? 0.0 : account_params[:latitude], :fb_id => account_params[:fb_id].blank? ? "" : account_params[:fb_id])
		if user.errors.any?
			return {:status => 0, :message => user.errors.messages.values.first.first}
		else
			return {:status => 1, :api_key => user.api_key}
		end
	end

	def upload_avatar(user, user_params)
		user = User.find(user.id)
		if user
			user.avatar = parse_image_data(user_params[:avatar_upload]) if user_params[:avatar_upload]
			user.save
			return {:status => 1, :avatar => "grid/#{user.avatar.url}"}
		else
			return {:status => 0}
		end
	end

	def add_remove_saved_shapp(user, params)
		product = Product.find(params[:product_id])
		if product.blank?
			return {:status => 0, :message => "Product does not exists"}
		else
			if params[:add_remove].to_i == 1
				if user.saved_shapps.include?(product)
					return {:status => 0, :message => "Already added to saved shapps"}
				else
					user.saved_shapps.create(:product => product)
					return {:status => 1, :message => "Added to saved shapps"}
				end
			else
				if user.saved_shapps.include?(product)
					user.saved_shapps.delete(product)
					return {:status => 1, :message => "Removed from saved shapps"}
				else
					return {:status => 0, :message => "Nothing to remove from saved shapps"}
				end
			end
		end
	end

	def add_bid_to_product(user, params)
		product = Product.find(params[:product_id])
		if product.blank?
			return {:status => 0, :message => "Product does not exists"}
		else
			if user.bids.where(:product_id => product.id, :bid_amount => params[:bid_amount]).count > 0
				return {:status => 0, :message => "Already added to my bids"}
			else
				user.bids.create(:product => product, :bid_amount => params[:bid_amount])
				return {:status => 1, :message => "Added to to my bids"}
			end
		end
	end

	def reject_product(user, params)
		product = Product.find(params[:product_id])
		if product.blank?
			return {:status => 0, :message => "Product does not exists"}
		else
			if user.rejected_products.include?(product)
				return {:status => 1, :message => "Product is already rejected"}
			else
				user.rejected_products.create(:product => product)
				return {:status => 0, :message => "Product Rejected"}
			end
		end
	end

	def get_my_products(user, params)
		if params[:product_type] == "saved_shapps"
			return {:status => 1, :products => user.saved_shapps.map{|saved_shapp| saved_shapp.product.to_discover(user)}}
		elsif params[:product_type] == "my_bids"
			return {:status => 1, :products => user.bids.map(&:product_id).uniq.map{|product_id| Product.find(product_id).to_discover(user)}}
		elsif params[:product_type] == "my_sales"
			return {:status => 1, :products => user.products.map{|product| product.to_discover(user)}}
		end
	end

	def get_product_bids(user, params)
		product = Product.find(params[:product_id])
		if product && product.user == user
			return {:status => 1, :bids => product.bids_to_api }
		else
			return {:status => 0}
		end
	end

	def get_product(apiuser, params)
		product = Product.find(params[:product_id])
		if product
			return {:status => 1, :product => product.to_discover(apiuser)}
		else
			return {:status => 0}
		end
	end

	def get_user_data(user)
		if user
			return {:status => 1, :user_data => user.to_api, :message => "User found"}
		else
			return {:status => 0, :message => "System cannot find your account. Please contact administrator for support"}
		end
	end

	def get_qualities
		qualities = Quality.all
		if qualities.count > 0
			return {:status => 1, :items => qualities.map{|quality| quality.to_api} }
		else
			return {:status => 0}
		end
	end

	def get_categories
		categories = Category.all
		if categories.count > 0
			return {:status => 1, :items => categories.map{|category| category.to_api} }
		else
			return {:status => 0}
		end
	end

	def get_brands(search_params)
		brands = Brand.any_of({:name => /^#{search_params[:query]}/i}).and(:category_id => search_params[:category_id])
		if brands.count > 0
			return {:status => 1, :items => brands.map{|brand| brand.to_api} }
		else
			return {:status => 0}
		end
	end

	def get_models(search_params)
		models = ItemModel.any_of({:name => /^#{search_params[:query]}/i}).and(:brand_id => search_params[:brand_id])
		if models.count > 0
			return {:status => 1, :items => models.map{|model| model.to_api} }
		else
			return {:status => 0}
		end
	end

	def update_profile(user, user_params)
		user = User.find(user.id)
		if user
			if user_params.has_key?(:first_name)
				if user_params[:first_name].blank?
					return {:status => 0, :message => "First name is required"}
				else
					user.update_attributes(:first_name => user_params[:first_name])
					return {:status => 1}
				end
			end
			if user_params.has_key?(:push_switch)
				user.update_attributes(:push_notification => user_params[:push_switch].to_i == 1 ? true : false)
				return {:status => 1}
			end
			if user_params.has_key?(:email_switch)
				user.update_attributes(:email_notification => user_params[:email_switch].to_i == 1 ? true : false)
				return {:status => 1}
			end
			if user_params.has_key?(:refresh_switch)
				user.update_attributes(:auto_refresh => user_params[:refresh_switch].to_i == 1 ? true : false)
				return {:status => 1}
			end
			if user_params.has_key?(:last_name)
				if user_params[:last_name].blank?
					return {:status => 0, :message => "Last name is required"}
				else
					user.update_attributes(:last_name => user_params[:last_name])
					return {:status => 1}
				end
			end
			if user_params.has_key?(:phone_number)
				if user_params[:phone_number].blank?
					return {:status => 0}
				else
					user.update_attributes(:phone_number => user_params[:phone_number])
					return {:status => 1}
				end
			end
			if user_params.has_key?(:email_address)
				if user_params[:email_address].blank?
					return {:status => 0, :message => "Email address is required"}
				else
					user.update_attributes(:email_address => user_params[:email_address])
					if user.errors.any?
						return {:status => 0, :message => user.errors.messages.values.first.first}
					else
						return {:status => 1}
					end
				end
			end
			if user_params.has_key?(:current_password) && user_params.has_key?(:new_password) && user_params.has_key?(:confirm_password)
				if decrypt_password(user.encrypted_password) == user_params[:current_password]
					if user_params[:new_password] == user_params[:confirm_password]
						user.update_attributes(:password => user_params[:new_password])
						return {:status => 1}
					else
						return {:status => 0, :message => "New password and confirm password do not match"}
					end
				else
					return {:status => 0, :message => "Current password do not match"}
				end
			end
			if user_params.has_key?(:country_name)
				country = Country.find_by(:name => user_params[:country_name])
				if country
					user.update_attributes(:country => country)
					return {:status => 1}
				else
					return {:status => 0, :message => "Country not found"}
				end
			end
			if user_params.has_key?(:state_name)
				state = State.find_by(:name => user_params[:state_name])
				if state
					user.update_attributes(:state => state)
					return {:status => 1}
				else
					return {:status => 0, :message => "State not found"}
				end
			end
			if user_params.has_key?(:city_name)
				city = City.find_by(:name => user_params[:city_name])
				if city
					user.update_attributes(:city => city)
					return {:status => 1}
				else
					return {:status => 0, :message => "City not found"}
				end
			end
		else
			return {:status => 0}
		end
	end

	def create_product(user, product_params)
		product = Product.create(:user => user, :product_name => product_params[:product_name], :product_description => product_params[:product_description], :min_bid_price => product_params[:min_bid_price], :buy_now_price => product_params[:buy_now_price], :bill_included => product_params[:bill_included].to_i == 1 ? true : false, :quality_id => product_params[:quality_id], :category_id => product_params[:category_id], :brand_id => product_params[:brand_id], :item_model_id => product_params[:model_id])
		if product.errors.any?
			return {:status => 0, :message => product.errors.messages.values.first.first}
		else
			if product_params.has_key?('bid_start') && product_params.has_key?('bid_end')
				product.update_attributes(:bid_start => DateTime.parse(product_params[:bid_start]), :bid_end => DateTime.parse(product_params[:bid_end]))
			end
			if product_params.has_key?('file_field_0')
				product_image = ProductImage.create(:product => product)
				product_image.image_upload = parse_image_data(product_params[:file_field_0]) if product_params[:file_field_0]
				product_image.save
			end
			if product_params.has_key?('file_field_1')
				product_image = ProductImage.create(:product => product)
				product_image.image_upload = parse_image_data(product_params[:file_field_1]) if product_params[:file_field_1]
				product_image.save
			end
			if product_params.has_key?('file_field_2')
				product_image = ProductImage.create(:product => product)
				product_image.image_upload = parse_image_data(product_params[:file_field_2]) if product_params[:file_field_2]
				product_image.save
			end
			if product_params.has_key?('file_field_3')
				product_image = ProductImage.create(:product => product)
				product_image.image_upload = parse_image_data(product_params[:file_field_3]) if product_params[:file_field_3]
				product_image.save
			end
			return {:status => 1}
		end
	end

	def discover_products(user, params)
		saved_shapps = user.saved_shapps.map(&:product_id)
		rejected_shapps = user.rejected_products.map(&:product_id)
		bids_products = user.bids.map(&:product_id)
		skiped_products = saved_shapps + rejected_shapps + bids_products
		required_products = Product.where(:id.nin => skiped_products).page(params[:page_number]).per(5)
		return {:status => 1, :products => required_products.map{|product| product.to_discover(user)}, :total_pages => required_products.total_pages}
	end

	def get_notifications(user, params)
		notifications = Notification.where(:user_id => user.id).order_by(:created_at => 'desc').page(params[:page_number]).per(50)
		return {:status => 1, :notifications => notifications.map{|notification| notification.to_api}, :total_pages => notifications.total_pages}
	end

	def product_comments(apiuser, params)
		product = Product.find(params[:product_id])
		if product.blank?
			return {:status => 0}
		else
			comments = product.comments.page(params[:page_number]).per(5).map{|comment| comment.to_api}
			return {:status => 1, :comments => comments, :total_pages => product.comments.count/5}
		end
	end

	def add_comment(apiuser, params)
		product = Product.find(params[:product_id])
		if product.blank?
			return {:status => 0}
		else
			comments = product.comments.create(:comment_text => params[:comment_text], :user => User.find(apiuser.id), :show_on_app => true)
			return {:status => 1}
		end
	end

	def update_search_profile(user, search_params)
		search_profile = user.search_profile
		if search_profile.blank?
			search_profile = user.search_profile.create(:user_id => user.id)
		end
		search_profile.update_attributes(:show_all => search_params[:show_all] == 1 ? true : false, :radius => search_params[:radius], :price_range => search_params[:price_range], :category => search_params[:category_id].blank? ? nil : Category.find(search_params[:category_id]), :quality => search_params[:quality_id].blank? ? nil : Quality.find(search_params[:quality_id]))
		return {:status => search_profile.errors.any? ? 0 : 1}
	end

	def add_product_view(apiuser, ip_address, params)
		product = Product.find(params[:product_id])
		if product.blank?
			return {:status => 0}
		else
			product.product_views.find_or_create_by(:ip_address => ip_address)
			return {:status => 1}
		end
	end

	def get_user_profile(apiuser, params)
		user = User.find(params[:user_id])
		if user.blank?
			return {:status => 0, :message => "User does not exists"}
		else
			return {:status => 1, :user_data => user.details_to_api(apiuser)}
		end
	end

	def follow_unfollow(apiuser, params)
		Rails.logger.info "Api: #{apiuser.id}"
		user = User.find(params[:user_id])
		if user.blank?
			return {:status => 0}
		else
			if apiuser.follower_of?(user)
				apiuser.unfollow(user)
				return {:status => 1}
			else
				apiuser.follow(user)
				return {:status => 1}
			end
		end
	end

	def parse_image_data(base64_image)
			current_date = DateTime.now
            filename = "upload-image-#{current_date.year}-#{current_date.month}-#{current_date.day}-#{current_date.hour}-#{current_date.min}"
            in_content_type, encoding, string = base64_image.split(/[:;,]/)[1..3]
            @tempfile = Tempfile.new(filename)
            @tempfile.binmode
            @tempfile.write Base64.decode64(base64_image)
            @tempfile.rewind
            content_type = `file --mime -b #{@tempfile.path}`.split(";")[0]
            extension = content_type.match(/gif|jpeg|png/).to_s
            filename += ".#{extension}" if extension
            ActionDispatch::Http::UploadedFile.new({
                tempfile: @tempfile,
                content_type: content_type,
                filename: filename
            })
    end


  	def clean_tempfile
    	if @tempfile
      		@tempfile.close
      		@tempfile.unlink
    	end
  	end
end
