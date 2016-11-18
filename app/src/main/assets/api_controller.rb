module Api::V1

    class ApiController < ApplicationController

        protect_from_forgery with: :null_session

        skip_before_filter  :verify_authenticity_token

        before_filter :setup_device

        def setup_device
            if apiuser
                push_enabled = request.headers['HTTP_X_API_PUSHENABLED']
                hardware_id = request.headers['HTTP_X_API_HARDWAREID']
                push_token = request.headers['HTTP_X_API_PUSHTOKEN']
                device_type_string = request.headers['HTTP_X_API_DEVICETYPE']
                device_type = 1
                if device_type_string == 'iPhone'
                    device_type = 1
                elsif device_type_string == 'iPad'
                    device_type = 2
                elsif device_type_string == 'Android'
                    device_type = 3
                else
                    device_type = 4
                end

                if push_enabled == "yes" && !hardware_id.blank? && !push_token.blank?
                    UserDevice.find_or_create_by(:device_type => device_type, :user => apiuser, :hardware_id => hardware_id, :push_token => push_token, :push_enabled => push_enabled == 'yes' ? true : false)
                end
            end
        end

        def apimodel
        	return Api::Modelv1.new
        end

        def apiuser
            api_key = request.headers['HTTP_X_API_KEY']
            api_email = request.headers['HTTP_X_API_USEREMAIL']
            return User.find_by(:email_address => api_email, :api_key => api_key)
        end

        def get_countries
        	render :json => ActiveSupport::JSON.encode(apimodel.get_countries(params))
        end

        def get_states
        	render :json => ActiveSupport::JSON.encode(apimodel.get_states(params))
        end

        def get_cities
        	render :json => ActiveSupport::JSON.encode(apimodel.get_cities(params))
        end

        def check_login_by_facebook
            render :json => ActiveSupport::JSON.encode(apimodel.check_login_by_facebook(params))
        end

        def check_login
        	render :json => ActiveSupport::JSON.encode(apimodel.check_login(params))
        end

        def create_account
        	render :json => ActiveSupport::JSON.encode(apimodel.create_account(params))
        end

        def upload_avatar
            render :json => ActiveSupport::JSON.encode(apimodel.upload_avatar(apiuser, params))
        end

        def get_user_data
            render :json => ActiveSupport::JSON.encode(apimodel.get_user_data(user))
        end

        def get_qualities
            render :json => ActiveSupport::JSON.encode(apimodel.get_qualities)
        end

        def get_categories
            render :json => ActiveSupport::JSON.encode(apimodel.get_categories)
        end

        def get_brands
            render :json => ActiveSupport::JSON.encode(apimodel.get_brands(params))
        end

        def get_models
            render :json => ActiveSupport::JSON.encode(apimodel.get_models(params))
        end

        def create_product
            render :json => ActiveSupport::JSON.encode(apimodel.create_product(apiuser, params))
        end

        def discover_products
            render :json => ActiveSupport::JSON.encode(apimodel.discover_products(apiuser, params))
        end

        def reject_product
            render :json => ActiveSupport::JSON.encode(apimodel.reject_product(apiuser, params))
        end

        def add_bid_to_product
            render :json => ActiveSupport::JSON.encode(apimodel.add_bid_to_product(apiuser, params))
        end

        def update_profile
            render :json => ActiveSupport::JSON.encode(apimodel.update_profile(apiuser, params))
        end

        def add_remove_saved_shapp
            render :json => ActiveSupport::JSON.encode(apimodel.add_remove_saved_shapp(apiuser, params))
        end

        def update_search_profile
            render :json => ActiveSupport::JSON.encode(apimodel.update_search_profile(apiuser, params))
        end

        def get_my_products
            render :json => ActiveSupport::JSON.encode(apimodel.get_my_products(apiuser, params))
        end

        def get_product
            render :json => ActiveSupport::JSON.encode(apimodel.get_product(apiuser, params))
        end

        def get_product_bids
            render :json => ActiveSupport::JSON.encode(apimodel.get_product_bids(apiuser, params))
        end

        def add_product_view
            render :json => ActiveSupport::JSON.encode(apimodel.add_product_view(apiuser, request.remote_ip, params))
        end

        def get_notifications
            render :json => ActiveSupport::JSON.encode(apimodel.get_notifications(apiuser, params))
        end

        def product_comments
            render :json => ActiveSupport::JSON.encode(apimodel.product_comments(apiuser, params))
        end

        def add_comment
            render :json => ActiveSupport::JSON.encode(apimodel.add_comment(apiuser, params))
        end

        def get_user_profile
            render :json => ActiveSupport::JSON.encode(apimodel.get_user_profile(apiuser, params))
        end

        def follow_unfollow
            render :json => ActiveSupport::JSON.encode(apimodel.follow_unfollow(apiuser, params))
        end
    end
end
