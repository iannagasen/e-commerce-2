def extract_code_from_url(url):
  # Find the start index of the 'code' parameter
  code_start_index = url.find('code=')
  if code_start_index != -1:
    # Find the end index of the 'code' parameter
    code_start_index += len('code=')
    code_end_index = url.find('&', code_start_index)
    
    # If no '&' is found, the code is till the end of the URL
    if code_end_index == -1:  
      code_end_index = len(url)
    
    # Extract the 'code' parameter value
    return url[code_start_index:code_end_index]
  else:
    return None